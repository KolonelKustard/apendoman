/*
 * ApendoServerGame.java
 *
 * Created on 18 November 2000, 11:54
 */

package apendoMan.server;

import javax.vecmath.*;

import apendoMan.networking.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ApendoServerGame extends Thread {    
    //private float TMP = 0;
    
    public static final int P_SERVER = 0;
    public static final int P_HEAD = 1;
    public static final int P_LEFT_HAND = 2;
    public static final int P_RIGHT_HAND = 3;
    public static final int P_LEFT_LEG = 4;
    public static final int P_RIGHT_LEG = 5;
    
    private ServerConnection gameClients[];
    
    private NetParser netParser;
    
    private Matrix4f objMats[];
    private Matrix4f oldMats[];
    
    private int matOwner[];

    /** Creates new ApendoServerGame */
    public ApendoServerGame(ServerConnection c[]) {
        gameClients = c;
        
        netParser = new NetParser();
        
        // Initialize the matrices...
        objMats = new Matrix4f[15];
        oldMats = new Matrix4f[15];
        
        for (int num = 0; num < 15; num++) {
            objMats[num] = new Matrix4f();
            oldMats[num] = new Matrix4f();
        }
        
        // Set up a corresponding 'ownership' array...
        matOwner = new int[15];
        
        matOwner[NetBehavior.PELVISTG] = P_SERVER;
        matOwner[NetBehavior.TORSOTG] = P_SERVER;
        
        matOwner[NetBehavior.HEADTG] = P_HEAD;
        
        matOwner[NetBehavior.LEFT_HANDTG] = P_LEFT_HAND;
        matOwner[NetBehavior.LEFT_UATG] = P_LEFT_HAND;
        matOwner[NetBehavior.LEFT_LATG] = P_LEFT_HAND;
        
        matOwner[NetBehavior.RIGHT_HANDTG] = P_RIGHT_HAND;
        matOwner[NetBehavior.RIGHT_UATG] = P_RIGHT_HAND;
        matOwner[NetBehavior.RIGHT_LATG] = P_RIGHT_HAND;
        
        matOwner[NetBehavior.LEFT_LEGTG] = P_LEFT_LEG;
        matOwner[NetBehavior.LEFT_ULTG] = P_LEFT_LEG;
        matOwner[NetBehavior.LEFT_LLTG] = P_LEFT_LEG;
        
        matOwner[NetBehavior.RIGHT_LEGTG] = P_RIGHT_LEG;
        matOwner[NetBehavior.RIGHT_ULTG] = P_RIGHT_LEG;
        matOwner[NetBehavior.RIGHT_LLTG] = P_RIGHT_LEG;
        
        for (int num = 0; num < gameClients.length; num++) {
            if (gameClients[num] != null) {
                netParser.newOutput(NetParser.CLIENT_NUM);
                netParser.addOutputParam(num);
                netParser.addOutputCommand(NetParser.GAME_START);
                netParser.addOutputParam("true");
                
                gameClients[num].sendData(netParser.getOutput());
            }
        }
        
        this.start();
    }
    
    private void adjustObjects(String s) {
        int tgnum;
        int interactorNum;
        
        netParser.newInput(s);
        for (int num = 0; num < netParser.getInputSize(); num++) {
            if (netParser.getInputCommand(num).equals(NetParser.TG_MAT)) {
                //System.out.println("Setting coords of the Torso");
                
                // This first number is the netObjs array position to Transform.  
                // The second is the players ID number.
                // The following 16 floats are the matrix for that transform.
                tgnum = netParser.getInputParamInt(num, 0);
                interactorNum = netParser.getInputParamInt(num, 1);
                
                objMats[tgnum].m00 = netParser.getInputParamFloat(num, 2);
                objMats[tgnum].m01 = netParser.getInputParamFloat(num, 3);
                objMats[tgnum].m02 = netParser.getInputParamFloat(num, 4);
                objMats[tgnum].m03 = netParser.getInputParamFloat(num, 5);
                objMats[tgnum].m10 = netParser.getInputParamFloat(num, 6);
                objMats[tgnum].m11 = netParser.getInputParamFloat(num, 7);
                objMats[tgnum].m12 = netParser.getInputParamFloat(num, 8);
                objMats[tgnum].m13 = netParser.getInputParamFloat(num, 9);
                objMats[tgnum].m20 = netParser.getInputParamFloat(num, 10);
                objMats[tgnum].m21 = netParser.getInputParamFloat(num, 11);
                objMats[tgnum].m22 = netParser.getInputParamFloat(num, 12);
                objMats[tgnum].m23 = netParser.getInputParamFloat(num, 13);
                objMats[tgnum].m30 = netParser.getInputParamFloat(num, 14);
                objMats[tgnum].m31 = netParser.getInputParamFloat(num, 15);
                objMats[tgnum].m32 = netParser.getInputParamFloat(num, 16);
                objMats[tgnum].m33 = netParser.getInputParamFloat(num, 17);
            }
        }
    }
    
    // The next method checks all the relevant game objects for changes in state, and if there is a change
    // adds a command to the output 
    private NetParser makeOutput(int player) {
        NetParser out = new NetParser();
        out.reset();
        
        for (int num = 0; num < objMats.length; num++) {
            
            // If the matrix has changed...
            if (!objMats[num].equals(oldMats[num])) {
                
                // And if the matrix we're looking at isn't owned by this player...
                if (matOwner[num] != player) {
                    
                    // Well then, add it to the output!
                    
                    // Add this TG to the outgoings...
                    out.addOutputCommand(NetParser.TG_MAT);
                    out.addOutputParam(num);
                    
                    // Let the server know which player this is coming from...
                    out.addOutputParam(P_SERVER);
                    
                    // Now add all the Matrix info to the outgoings...
                    out.addOutputParam(objMats[num].m00);
                    out.addOutputParam(objMats[num].m01);
                    out.addOutputParam(objMats[num].m02);
                    out.addOutputParam(objMats[num].m03);
                    out.addOutputParam(objMats[num].m10);
                    out.addOutputParam(objMats[num].m11);
                    out.addOutputParam(objMats[num].m12);
                    out.addOutputParam(objMats[num].m13);
                    out.addOutputParam(objMats[num].m20);
                    out.addOutputParam(objMats[num].m21);
                    out.addOutputParam(objMats[num].m22);
                    out.addOutputParam(objMats[num].m23);
                    out.addOutputParam(objMats[num].m30);
                    out.addOutputParam(objMats[num].m31);
                    out.addOutputParam(objMats[num].m32);
                    out.addOutputParam(objMats[num].m33);
                }
            }
        }
        
        return out;
    }
       
    public void run() {
        String s;
        
        System.out.println("ApendoServerGame Started");
        
        while (true) {
            
            // First for loop is to grab any new data from the users.
            for (int num = 0; num < 5; num++) {
                if (gameClients[num] != null) {
                    s = gameClients[num].getData();
                    if (s != null) {
                        // The next line can be uncommented to see what input is coming from who.
                        //System.out.println("In:  Client: " + num + ":  " + s);
                        adjustObjects(s);
                    }
                }
            }
            
            // The next bit then checks which objects have changed and makes an outgoing NetParser object...

            // I'm afraid I'm just implementing this nasty method for sending back to the clients!
            // I've just written if statements for the different client players.
            // The next five if statements take the existing output, and remove the data that isn't needed by that particular object.
            // This is so the client code can be cleaner, and doesn't have to constantly check if the data its receiving will
            // monkey about with the models in their game.
            
            if (gameClients[P_SERVER] != null) {
                netParser.reset();
                netParser = makeOutput(P_SERVER);
                if (netParser.getOutput() != null) {
                    gameClients[P_SERVER].sendData(netParser.getOutput());
                    //System.out.println("Out: Server: " + netParser.getOutput());
                }
            }

            if (gameClients[P_HEAD] != null) {
                netParser.reset();
                netParser = makeOutput(P_HEAD);
                if (netParser.getOutput() != null) {
                    gameClients[P_HEAD].sendData(netParser.getOutput());
                    //System.out.println("Out: Head: " + netParser.getOutput());
                }
            }
            
            if (gameClients[P_LEFT_HAND] != null) {
                netParser.reset();
                netParser = makeOutput(P_LEFT_HAND);
                if (netParser.getOutput() != null) {
                    gameClients[P_LEFT_HAND].sendData(netParser.getOutput());
                    //System.out.println("Out: Left Hand: " + netParser.getOutput());
                }
            }
            
            if (gameClients[P_RIGHT_HAND] != null) {
                netParser.reset();
                netParser = makeOutput(P_RIGHT_HAND);
                if (netParser.getOutput() != null) {
                    gameClients[P_RIGHT_HAND].sendData(netParser.getOutput());
                    //System.out.println("Out: Right Hand: " + netParser.getOutput());
                }
            }
            
            if (gameClients[P_LEFT_LEG] != null) {
                netParser.reset();
                netParser = makeOutput(P_LEFT_LEG);
                if (netParser.getOutput() != null) {
                    gameClients[P_LEFT_LEG].sendData(netParser.getOutput());
                    //System.out.println("Out: Left Leg: " + netParser.getOutput());
                }
            }
            
            if (gameClients[P_RIGHT_LEG] != null) {
                netParser.reset();
                netParser = makeOutput(P_RIGHT_LEG);
                if (netParser.getOutput() != null) {
                    gameClients[P_RIGHT_LEG].sendData(netParser.getOutput());
                    //System.out.println("Out: Right Leg: " + netParser.getOutput());
                }
            }
            
            // reset states of all objects AFTER doing the sending...
            
            for (int num = 0; num < objMats.length; num++) {
                oldMats[num].set(objMats[num]);
            }
            
            
            // Then make the thread sleep to avoid caning the system!
            try {
                this.sleep(10);
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

}