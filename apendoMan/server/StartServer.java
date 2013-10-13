/*
 * StartServer.java
 *
 * Created on 09 November 2000, 17:54
 */

package apendoMan.server;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import apendoMan.networking.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class StartServer extends Thread {

    public static int PORT = 7879;

    //private ListenClientConnection listener;
    private ServerConnection gameClients[] = {null, null, null, null, null, null};
    private int numOfClients = 0;
    
    private ServerSocket sock;
    private Socket socket;
    private NetParser netParser;
    
    private TextArea outputBox;
    
    private boolean createSClient;

    public StartServer(TextArea o, boolean createSClient) {
        outputBox = o;
        this.createSClient = createSClient;
        netParser = new NetParser();
        this.start();
    }

    public void run() {
        // Open server socket...
        try {
            outputBox.append("Opening server on port: " + PORT + "\n");
            sock = new ServerSocket(PORT);
            
            // First client to join must be the server client (controls all physics)
            if (createSClient) {
                outputBox.append("Port opened.  Now waiting for server client to begin.\n");
                socket = sock.accept();
                gameClients[numOfClients] = new ServerConnection(socket);
                outputBox.append("Server game has joined.\n\n");
            }
            numOfClients++;
            
            outputBox.append("Now listening for clients...\n\n");

            // Listen for client connections until 5 players exist...
            while(numOfClients <= 5) {
                try {
                    socket = sock.accept();
                    gameClients[numOfClients] = new ServerConnection(socket);

                    outputBox.append("Added player no.: " + numOfClients + "\n");

                    // Send some confirmatory stuff to the client...
                    netParser.newOutput(NetParser.PLAIN_TEXT, "Welcome to the Robot Game Server");
                    gameClients[numOfClients].sendData(netParser.getOutput());

                    numOfClients++;
                }
                catch (IOException E) {
                    outputBox.append(E.toString());
                }
            }

        }
        catch (IOException E) {
            outputBox.append(E.toString());
        }

        if (numOfClients == 6) {
            outputBox.append("5 players accounted for.\n");
            this.startGame();
        }
        else {
            outputBox.append("Have somehow inexplicably got this far with: " + numOfClients + "players.\n");
        }
    }
    
    public void startGame() {
        outputBox.append("Initialising game...\n");
        new ApendoServerGame(gameClients);
    }

}
