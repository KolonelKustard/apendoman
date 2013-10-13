/*
 * ClientConnections.java
 *
 * Created on 03 October 2000, 23:39
 *
 * This class is for specific connection methods relating to the Robot game.
 */

package apendoMan.server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ServerConnection extends Thread {
   
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String back = "";
    private String oldBack = "";
    private String userID;
    
    /** Creates new ClientConnections */
    public ServerConnection(Socket sock) throws IOException {
        
        socket = sock;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        start();
    }
    
    public void run () {
        while (true) {
            try {
                back = in.readLine();
            }
            catch (IOException E) {
                System.out.println("Something arse-up happened whilst getting data from the server...:\n" + E);
                System.exit(1);
            }
        }
    }
    
    public void sendData (String s) {
        out.println (s);
    }
    
    public String getData () {
        if (oldBack.equals(back)) {
            return null;
        }
        else {
            oldBack = back;
            return back;
        }
    }
    
    public void setUserID (String s) {
        userID = s;
    }
    
    public String getUserID () {
        return userID;
    }
    
    public void disconnect () throws IOException {
        socket.close();
    }

}
