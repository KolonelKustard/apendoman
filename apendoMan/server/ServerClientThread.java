/*
 * ServerClientThread.java
 *
 * Created on 20 January 2000, 23:29
 */

package apendoMan.server;

import java.awt.Frame;

import apendoMan.networking.*;
import apendoMan.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ServerClientThread extends Thread {
    ApendoManClient serverClient;
    ConnectToServer conn;
    
    Frame parent;

    /** Creates new ServerClientThread */
    public ServerClientThread(Frame parent) {
        this.parent = parent;
        
        try {
            conn = new ConnectToServer("127.0.0.1", StartServer.PORT);
        }
        catch (Exception e) {
            System.out.println("Oh dear, game will not work.  Couldn't connect server client");
        }
        
        start();
    }
    
    public void run() {
        serverClient = new ApendoManClient(parent, conn, ApendoServerGame.P_SERVER);
    }

}
