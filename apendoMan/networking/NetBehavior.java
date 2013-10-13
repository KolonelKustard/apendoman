/*
 * RotateApendoBit.java
 *
 * Created on 21 November 2000, 18:40
 */

package apendoMan.networking;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.event.*;
import java.util.Enumeration;

/**
 *
 * @author  Kolonel Kustard
 * @version
 */

public class NetBehavior extends Behavior {
    public static final int PELVISTG = 0;
    
    public static final int TORSOTG = 1;
    public static final int HEADTG = 2;
    public static final int LEFT_HANDTG = 3;
    public static final int RIGHT_HANDTG = 4;
    public static final int LEFT_FOOTTG = 5;
    public static final int RIGHT_FOOTTG = 6;
    
    public static final int LEFT_ULTG = 7;
    public static final int RIGHT_ULTG = 8;
    public static final int LEFT_LLTG = 9;
    public static final int RIGHT_LLTG = 10;
    public static final int LEFT_UATG = 11;
    public static final int RIGHT_UATG = 12;
    public static final int LEFT_LATG = 13;
    public static final int RIGHT_LATG = 14;
    
    private static final long timetowait = 10; // time in milliseconds before checking server again...
    
    WakeupCriterion criterion[] = {new WakeupOnElapsedFrames(1)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    private int interactor;
    
    private ConnectToServer conn;
    private String stringIn;
    private NetParser netParser;
    
    private TransformGroup netObjs[];
    private Matrix4f oldMat[];
    
    private Transform3D trans3d = new Transform3D();
    private Matrix4f mat = new Matrix4f();
    
    // create SimpleBehavior
    public NetBehavior(ConnectToServer c, int interactor, TransformGroup netObjs[]){
        this.netObjs = netObjs;
        this.interactor = interactor;
        conn = c;
        
        oldMat = new Matrix4f[netObjs.length];
        
        for (int num = 0; num < netObjs.length; num++) {
            oldMat[num] = new Matrix4f();
            
            netObjs[num].getTransform(trans3d);
            trans3d.get(mat);
            oldMat[num].set(mat);
        }
        
        netParser = new NetParser();
        
    }
    
    private void parseInput(String s) {
        int tgnum;
        int interactorNum;
        
        //System.out.println("New input from server");
        
        netParser.newInput(s);
        for (int num = 0; num < netParser.getInputSize(); num++) {
            if (netParser.getInputCommand(num).equals(NetParser.TG_MAT)) {
                
                // This first number is the netObjs array position to Transform.
                // The second number is the receiving interactor number (in case of client, should equal server).
                // The following 16 floats are the matrix for that transform.
                tgnum = netParser.getInputParamInt(num, 0);
                interactorNum = netParser.getInputParamInt(num, 1);
                
                mat.m00 = netParser.getInputParamFloat(num, 2);
                mat.m01 = netParser.getInputParamFloat(num, 3);
                mat.m02 = netParser.getInputParamFloat(num, 4);
                mat.m03 = netParser.getInputParamFloat(num, 5);
                mat.m10 = netParser.getInputParamFloat(num, 6);
                mat.m11 = netParser.getInputParamFloat(num, 7);
                mat.m12 = netParser.getInputParamFloat(num, 8);
                mat.m13 = netParser.getInputParamFloat(num, 9);
                mat.m20 = netParser.getInputParamFloat(num, 10);
                mat.m21 = netParser.getInputParamFloat(num, 11);
                mat.m22 = netParser.getInputParamFloat(num, 12);
                mat.m23 = netParser.getInputParamFloat(num, 13);
                mat.m30 = netParser.getInputParamFloat(num, 14);
                mat.m31 = netParser.getInputParamFloat(num, 15);
                mat.m32 = netParser.getInputParamFloat(num, 16);
                mat.m33 = netParser.getInputParamFloat(num, 17);
                
                // Now we have a matrix for transform[num], we just have to apply it...
                trans3d.set(mat);
                netObjs[tgnum].setTransform(trans3d);
                
                // Set the "old" matrix to equal this one so we know it has changed...
                oldMat[tgnum].set(mat);
            }
        }
    }
    
    // initialize the Behavior
    //     set initial wakeup condition
    //     called when behavior becomes live
    public void initialize(){
        // set initial wakeup condition
        this.wakeupOn(conditions);
    }
    
    // behave
    // called by Java 3D when appropriate stimulus occures
    public void processStimulus(Enumeration criteria){
        
        stringIn = conn.getData();
        if (stringIn != null) {
            parseInput(stringIn);
        }
        
        sendToServer();
        
        this.wakeupOn(conditions);
    }
    
    public void sendToServer() {
        netParser.reset();

        // Run through all TG's and check to see if they have changed.  If they have,
        // send the new Matrix info to the server...
        for (int num = 0; num < netObjs.length; num++) {
            netObjs[num].getTransform(trans3d);
            trans3d.get(mat);
            
            if (!mat.equals(oldMat[num])) {
                
                // Add this TG to the outgoings...
                netParser.addOutputCommand(NetParser.TG_MAT);
                netParser.addOutputParam(num);
                
                // Let the server know which player this is coming from...
                netParser.addOutputParam(interactor);
                
                // Now add all the Matrix info to the outgoings...
                netParser.addOutputParam(mat.m00);
                netParser.addOutputParam(mat.m01);
                netParser.addOutputParam(mat.m02);
                netParser.addOutputParam(mat.m03);
                netParser.addOutputParam(mat.m10);
                netParser.addOutputParam(mat.m11);
                netParser.addOutputParam(mat.m12);
                netParser.addOutputParam(mat.m13);
                netParser.addOutputParam(mat.m20);
                netParser.addOutputParam(mat.m21);
                netParser.addOutputParam(mat.m22);
                netParser.addOutputParam(mat.m23);
                netParser.addOutputParam(mat.m30);
                netParser.addOutputParam(mat.m31);
                netParser.addOutputParam(mat.m32);
                netParser.addOutputParam(mat.m33);
                
                // Make the "old" matrix equal the new one.  So we don't keep sending data to the server.
                oldMat[num].set(mat);
            }
        }
        
        if (netParser.getOutput() != null) {
            conn.sendData(netParser.getOutput());
        }
    }
    
}