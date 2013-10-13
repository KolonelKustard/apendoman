/*
 * ConnectToServer.java
 *
 * Created on 04 October 2000, 14:26
 */

package apendoMan.networking;

import java.io.*;
import java.net.*;

/**
 *
 * @author  Kolonel Kustard
 * @version
 */
public class ConnectToServer extends Thread {
    
    private boolean isNullConn = false;
    private boolean isDebug = false;
    
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    
    private String stringIn = "";
    private String lastDataIn = "";
    
    /** Creates new Connection to Server */
    public ConnectToServer(String serverIP, int serverPort) throws IOException, SecurityException {
        
        // Set up Socket connection and then set up input/output streams...
        
        InetAddress addr = InetAddress.getByName(serverIP);
        socket = new Socket(addr, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
        start();
    }
    
    public ConnectToServer(String nullConn) {
        if (nullConn.equals("DEBUG")) {
            isNullConn = true;
            isDebug = true;
        }
        
        if (nullConn.equals("NULL")) {
            isNullConn = true;
            isDebug = false;
        }
    }
    
    public void run() {
        if (!isNullConn) {
            while (true) {
                try {
                    stringIn = in.readLine();
                }
                catch (IOException e) {
                    System.out.println("Connection died with error: " + e);
                    System.exit(1);
                }
            }
        }
        else {
            while (true) {
                try {
                    this.sleep(10000);
                }
                catch (Exception e) {
                }
            }
        }
    }
    
    public void sendData (String s) {
        if (!isNullConn) {
            out.println (s);
        }
        else {
            if (isDebug) {
                System.out.println("Connection output: " + s);
            }
        }
    }
    
    public String getData() {
        if (lastDataIn.equals(stringIn)) {
            return null;
        }
        else {
            lastDataIn = stringIn;
            return stringIn;
        }
    }
    
}
