/*
 * BehaviorServer.java
 *
 * Created on 20 January 2000, 22:39
 */

package apendoMan.server;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

import apendoMan.networking.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class BehaviorServer extends Behavior {
    public static final boolean DEBUG = true;
    public static final float WEIGHTING_FACTOR = 0.15f;
    public static final float ROTATION_FACTOR = 0.0002f;
    
    public static final float MAX_ROT_X = 1.5f;
    public static final float MIN_ROT_X = -1.5f;
    public static final float MAX_ROT_Z = 1.5f;
    public static final float MIN_ROT_Z = -1.5f;
    
    protected WakeupCriterion criterion[] = {new WakeupOnElapsedFrames(1)};
    protected WakeupCondition conditions = new WakeupOr(criterion);
     
    protected TransformGroup[] allTGs;
    protected TransformGroup[] weightedTGs = new TransformGroup[allTGs.length];
    protected float[] weights = new float[allTGs.length];
    
    protected BranchGroup rootBG;
    
    protected TransformGroup bottomArcTG;
    protected TransformGroup topArcTG, topArcTGL;
    protected TransformGroup cogTG;
    
    protected float rotX, rotZ;
    
    protected ResetWindow resetWindow;

    /** Creates new BehaviorServer */
    public BehaviorServer(TransformGroup[] allTGs, BranchGroup rootBG) {
        this.allTGs = allTGs;
        this.rootBG = rootBG;
        
        rotX = rotZ = 0.0f;
        
        addWeightedTGs();
        setTGWeights();
        initCogTG();
        initArcTGs();
        
        resetWindow = new ResetWindow(this);
        resetWindow.setSize(100, 40);
        resetWindow.show();
    }
    
    protected void reset() {
        Transform3D resetTrans = new Transform3D();
        Transform3D topArcTrans = new Transform3D();
        
        bottomArcTG.setTransform(resetTrans);
        
        topArcTG.getLocalToVworld(topArcTrans);
        allTGs[NetBehavior.PELVISTG].setTransform(topArcTrans);
    }
    
    protected void addWeightedTGs() {
        for (int num = 0; num < allTGs.length; num++) {
            weightedTGs[num] = new TransformGroup();
            weightedTGs[num].setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
            
            allTGs[num].addChild(weightedTGs[num]);
            if (DEBUG) allTGs[num].addChild(new com.sun.j3d.utils.geometry.ColorCube(5.0));
        }
    }
    
    protected void setTGWeights() {
        // The weights system doesn't emulate anything in particular...  I suppose you could use
        // tonnes though seeing as how freakin' massiv apendoJustin is...
        
        // Need to initialise all weights to 0
        for (int num = 0; num < weights.length; num++) {
            weights[num] = 0.0f;
        }
        
        weights[NetBehavior.HEADTG] = 0.5f;
        weights[NetBehavior.LEFT_HANDTG] = 0.6f;
        weights[NetBehavior.RIGHT_HANDTG] = 0.6f;
        weights[NetBehavior.LEFT_FOOTTG] = 1.0f;
        weights[NetBehavior.RIGHT_FOOTTG] = 1.0f;
    }
    
    protected void initCogTG() {
        // Add center of gravity TG
        cogTG = new TransformGroup();
        cogTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        cogTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        if (BehaviorServer.DEBUG) cogTG.addChild(new com.sun.j3d.utils.geometry.ColorCube(20.0));
        rootBG.addChild(cogTG);
    }
    
    protected void initArcTGs() {
        Transform3D pelvisTrans;
        Vector3f pelvisVec;
        Transform3D trans;
        Vector3f vec;
        
        bottomArcTG = new TransformGroup();
        if (DEBUG) bottomArcTG.addChild(new com.sun.j3d.utils.geometry.ColorCube(20.0));
        topArcTGL = new TransformGroup();
        topArcTG = new TransformGroup();
        if (DEBUG) topArcTG.addChild(new com.sun.j3d.utils.geometry.ColorCube(20.0));
        pelvisTrans = new Transform3D();
        pelvisVec = new Vector3f();
        trans = new Transform3D();
        vec = new Vector3f();
        
        bottomArcTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        topArcTG.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        
        allTGs[NetBehavior.PELVISTG].getTransform(pelvisTrans);
        pelvisTrans.get(pelvisVec);
        
        vec.x = pelvisVec.x;
        vec.y = 0.0f;
        vec.z = pelvisVec.z;
        
        trans.setTranslation(vec);
        bottomArcTG.setTransform(trans);
        
        trans = new Transform3D();
        trans.setTranslation(pelvisVec);
        topArcTGL.setTransform(trans);
        
        topArcTGL.addChild(topArcTG);
        bottomArcTG.addChild(topArcTGL);
        rootBG.addChild(bottomArcTG);
    }
    
    public void initialize(){
        wakeupOn(new WakeupOnElapsedFrames(0));
    }
    
    protected void adjustCOG() {
        float cogX, cogY, cogZ;
        Transform3D pelvisTrans;
        Vector3f pelvisVec;
        Transform3D cogTrans;
        Vector3f cogVec;
        Transform3D trans;
        Vector3f vec;
        
        cogX = cogY = cogZ = 0.0f;
        pelvisTrans = new Transform3D();
        pelvisVec = new Vector3f();
        cogTrans = new Transform3D();
        cogVec = new Vector3f();
        trans = new Transform3D();
        vec = new Vector3f();
        
        weightedTGs[NetBehavior.PELVISTG].getLocalToVworld(pelvisTrans);
        pelvisTrans.get(pelvisVec);
        
        // Make changes according to all limb parts...
        for (int num = 0; num < weightedTGs.length; num++) {
            weightedTGs[num].getLocalToVworld(trans);
            trans.get(vec);
        
            cogX += ((pelvisVec.x + vec.x) * weights[num]);
            cogZ += ((pelvisVec.z + vec.z) * weights[num]);
        }
        
        // Update centre of gravity TG
        cogVec.x = cogX * WEIGHTING_FACTOR;
        cogVec.y = pelvisVec.y;
        cogVec.z = cogZ * WEIGHTING_FACTOR;
        cogTrans.setTranslation(cogVec);
        cogTG.setTransform(cogTrans);
    }
    
    // This method uses the centre of gravity TG to rotate the bottomArcTG TG.  This in turn orbits
    // the topArcTG TG which then represents the new position and rotation for the pelvis (the
    // root of our ApendoMon).
    protected void adjustOurMan() {
        Transform3D topArcTrans;
        Transform3D bottomArcTrans;
        Transform3D cogTrans;
        Vector3f cogVec;
        Transform3D rotXTrans, rotZTrans;
        
        topArcTrans = new Transform3D();
        bottomArcTrans = new Transform3D();
        cogTrans = new Transform3D();
        cogVec = new Vector3f();
        rotXTrans = new Transform3D();
        rotZTrans = new Transform3D();
        
        // Get Vector from centre of gravity TG
        cogTG.getTransform(cogTrans);
        cogTrans.get(cogVec);
        
        rotX += cogVec.z * ROTATION_FACTOR;
        rotZ -= cogVec.x * ROTATION_FACTOR;
        
        if ((rotX <= MAX_ROT_X) && (rotX >= MIN_ROT_X)) {
            rotXTrans.rotX(rotX);
        }
        else {
            if (rotX > MAX_ROT_X) rotXTrans.rotX(MAX_ROT_X);
            if (rotX < MIN_ROT_X) rotXTrans.rotX(MIN_ROT_X);
        }
        
        if ((rotZ <= MAX_ROT_Z) && (rotZ >= MIN_ROT_Z)) {
            rotZTrans.rotZ(rotZ);
        }
        else {
            if (rotZ > MAX_ROT_Z) rotXTrans.rotZ(MAX_ROT_Z);
            if (rotZ < MIN_ROT_Z) rotXTrans.rotZ(MIN_ROT_Z);
        }
        
        bottomArcTrans.mul(rotZTrans);
        bottomArcTrans.mul(rotXTrans);
        
        bottomArcTG.setTransform(bottomArcTrans);
        
        topArcTG.getLocalToVworld(topArcTrans);
        allTGs[NetBehavior.PELVISTG].setTransform(topArcTrans);
    }
    
    public void processStimulus(Enumeration criteria){
        adjustCOG();
        adjustOurMan();
        wakeupOn(conditions);
    }
    
}
