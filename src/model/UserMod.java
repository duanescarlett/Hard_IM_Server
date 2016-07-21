package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Duane on 03/07/2016.
 */
public class UserMod extends DbConnector{


    public UserMod() {
        super();
    }

    public ResultSet allUsers(){

        try {
            // 3. Execute SQL query
            this.sql =  mySql.executeQuery("SELECT * FROM profile");

            // 4. Process the result set
            while(sql.next()){
                System.out.println("(UserMod.java): Database operation was successful");
                System.out.println("Users :> " + sql.getString("username"));
            }

            return this.sql;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return this.sql = null;
        }

    }

    public ResultSet user(String id){
        try {
            this.sql =  mySql.executeQuery("SELECT * FROM profile WHERE id_key='id'");
            return this.sql;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return this.sql = null;
        }
    }

    public boolean Insert(String statement){
        try {
            mySql.executeUpdate(statement);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet get(String queryString){
        this.sql = null;
        try {
            this.sql =  mySql.executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.sql;

    }
}








