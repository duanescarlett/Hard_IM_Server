package logic;

import java.io.Serializable;

/**
 * Created by Duane on 25/05/2016.
 */
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    private String type;
    private String message;

    ChatMessage() {}

    // getters
    protected String getType() {
        return type;
    }

    protected String getMessage() {
        return message;
    }

    protected void setMessage(String s){
        this.message = s;
    }

    protected void setType(String s){
        this.type = s;
    }
}


