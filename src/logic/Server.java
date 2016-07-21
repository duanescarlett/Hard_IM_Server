package logic;

import model.UserMod;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
    // a unique ID for each connection
    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> clientThreads;

    // the boolean that will be turned of to stop the server
    private boolean keepGoing;
    private String[] stringParts;

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "scarstar";

    // Database URL
    static final String URL = "jdbc:mysql://localhost:3306/fire_brigade";

    /*
     *  server constructor that receive the port to listen to for connection as parameter
     *  in console
     */
    public Server() throws Exception {
        // ArrayList for the Client list
        clientThreads = new ArrayList<>();
        clientThreads.add(new ClientThread());
    }


    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < clientThreads.size(); ++i) {
            ClientThread ct = clientThreads.get(i);
            // found it
            if(ct.id == id) {
                clientThreads.remove(i);
                return;
            }
        }
    }

    /** One instance of this thread will run for each client */
    class ClientThread extends Thread {
        private int port;
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // my unique id (easier for disconnection)
        int id;
        // the Username of the Client
        String username;
        // the only type of message a will receive
        ChatMessage cm;
        // the date I connect
        String date;
        ServerSocket serverSocket;
        UserMod userMod;

        // Constructor
        ClientThread() throws IOException, SQLException {
            this.port = 6066;
            this.setUpSocketsAndStreams();
            userMod = new UserMod();
            userMod.allUsers();
            this.start();
        }

        synchronized void setUpSocketsAndStreams(){
            id = ++uniqueId;
            try {
                this.serverSocket = new ServerSocket(this.port, 100);
                this.socket = serverSocket.accept(); // accept connection
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                System.out.println("(Server.java): Sockets and Streams created, a new client is connected");
                System.out.println("(Server.java): A new client is connected");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // what will run forever
        public void run() {

            // to loop until LOGOUT
            boolean keepGoing = true;
            //this.sendUsers();

            try {
                waitForConnection();

                while(keepGoing) {
                    parser();
                }

                this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.close();
        }

        private void parser(){

            String string = null;
            try {
                string = sInput.readObject().toString();
            } catch (IOException e) { e.printStackTrace(); }
            catch (ClassNotFoundException e) { e.printStackTrace(); }

            stringParts = string.split(":", 2);
            System.out.println(stringParts[0]);

            switch(stringParts[0]){
                case "Sign Up":

                    if(this.signUp()) { // User is signing up
                        System.out.println("(Server.java): User signed up");
                    }
                    break;

                case "Login":
                    //
                    login(stringParts[1]);
                    break;

                default:
                    // Start listening for chat

            }

            whileChatting();
        }

        private boolean signUp(){
            System.out.println(stringParts[1]);
            boolean ans = userMod.Insert(stringParts[1]);
            return ans;
        }

        private void login(String s) {
            if(userMod.get(s) != null){
                try {
                    sOutput.writeObject("LIS");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void waitForConnection() throws IOException{
            showMessage("I just made a connection\n");
            showMessage("Now connected to " + this.socket.getInetAddress().getHostName() + "\n");
        }

        private void whileChatting() {
            String message = "My name is server, hi :)\n";
            sendMessage(message);
            // Send message to other client
            do{
                try{
                    message = sInput.readObject().toString();
                    sendMessage(message);
                }
                catch (ClassNotFoundException c){
                    showMessage("idk wtf the user said! \n");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (!message.equals("die"));
        }

        private void sendUsers(){
            try{
                sOutput.writeObject("User Stack:" + userMod.allUsers().toString());
            }
            catch (IOException io){

            }
        }

        private void sendMessage(String messaage){
            try{
                sOutput.writeObject("Server:" + messaage);
                sOutput.flush();
            }
            catch (IOException io){
                //chatWindow.append("\n ERROR: Dude i cant send that message");
            }
        }

        private void showMessage(final String text){
            System.out.print(text);
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception e) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if(!socket.isConnected()) {
                try {
                    close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {

            }
            return true;
        }
    }
}