/*
 * ApendoPlayer.java
 *
 * Created on 01 November 2000, 17:25
 */

package apendoMan;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.mnstarfire.loaders3d.Inspector3DS;

import apendoMan.apendoBits.*;
import apendoMan.networking.*;
import apendoMan.server.*;

/**
 *
 * @author  Kolonel Kustard
 * @version
 */
public class ApendoPlayer extends BranchGroup {
    public static final int SERVER = apendoMan.server.ApendoServerGame.P_SERVER;        // These use the same numbers as from the server app to ensure consistency...
    public static final int HEAD = apendoMan.server.ApendoServerGame.P_HEAD;            // Because they'returntheortically 2 different apps (server and client), I
    public static final int LEFT_HAND = apendoMan.server.ApendoServerGame.P_LEFT_HAND;  // though I'd best seperate them too.
    public static final int RIGHT_HAND = apendoMan.server.ApendoServerGame.P_RIGHT_HAND;
    public static final int LEFT_LEG = apendoMan.server.ApendoServerGame.P_LEFT_LEG;
    public static final int RIGHT_LEG = apendoMan.server.ApendoServerGame.P_RIGHT_LEG;
    
    // The following number represents a special 1-player state used for testing the server physics.
    public static final int SERVER_DEBUG = 10000;
    
    private final int LOADER_DEBUG = 0;
    
    // Player number...
    private int interactor;
    
    // Pointer to the viewing platform.
    private TransformGroup vpTrans = null;
    
    // The root of the graph...
    private TransformGroup apendoPelvisTG;
    
    // Main player objects...
    private TransformGroup apendoHeadTG;
    private TransformGroup apendoTorsoTG;
    private TransformGroup apendoLHTG; // Left Hand
    private TransformGroup apendoRHTG; // Right Hand
    private TransformGroup apendoLFTG; // Left Foot
    private TransformGroup apendoRFTG; // Right Foot
    
    // Other components...
    private TransformGroup apendoLULTG; // Left upper leg
    private TransformGroup apendoRULTG; // Right upper leg
    private TransformGroup apendoLLLTG; // Left lower leg
    private TransformGroup apendoRLLTG; // Right lower leg
    private TransformGroup apendoLUATG; // Left upper arm
    private TransformGroup apendoRUATG; // Right upper arm
    private TransformGroup apendoLLATG; // Left lower arm
    private TransformGroup apendoRLATG; // Right lower arm
    
    private TransformGroup netObjs[] = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    
    private NetBehavior netBehavior;
    
    private BoundingSphere bounds;
    
    private ConnectToServer conn;
    
    /** Creates new ApendoPlayer */
    public ApendoPlayer(ConnectToServer conn, int interactor, TransformGroup vpTrans) {
        this.conn = conn;
        this.interactor = interactor;
        this.vpTrans = vpTrans;
        
        bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), Double.MAX_VALUE); // This bounds is for all objects.  Never want ApendoMan to be un seen/controlled.
        
        // Load all models into TG's...
        loadModels();
        
        // Set all the correct capability bits for the TG's...
        setTGCapabilities();

        // Build the scene graph...
        buildSceneGraph();
        
        // Set the initial positions...
        positionTGs();
        
        netObjs[NetBehavior.PELVISTG] = apendoPelvisTG;
        netObjs[NetBehavior.HEADTG] = apendoHeadTG;
        netObjs[NetBehavior.TORSOTG] = apendoTorsoTG;
        netObjs[NetBehavior.LEFT_HANDTG] = apendoLHTG;
        netObjs[NetBehavior.RIGHT_HANDTG] = apendoRHTG;
        netObjs[NetBehavior.LEFT_FOOTTG] = apendoLFTG;
        netObjs[NetBehavior.RIGHT_FOOTTG] = apendoRFTG;
        netObjs[NetBehavior.LEFT_ULTG] = apendoLULTG;
        netObjs[NetBehavior.RIGHT_ULTG] = apendoRULTG;
        netObjs[NetBehavior.LEFT_LLTG] = apendoLLLTG;
        netObjs[NetBehavior.RIGHT_LLTG] = apendoRLLTG;
        netObjs[NetBehavior.LEFT_UATG] = apendoLUATG;
        netObjs[NetBehavior.RIGHT_UATG] = apendoRUATG;
        netObjs[NetBehavior.LEFT_LATG] = apendoLLATG;
        netObjs[NetBehavior.RIGHT_LATG] = apendoRLATG;
        
        switch (interactor) {
            case SERVER :
                playAsServer();
                break;
            
            case HEAD :
                playAsHead();
                break;
                
            case LEFT_HAND :
                playAsLeftHand();
                break;
                
            case RIGHT_HAND :
                playAsRightHand();
                break;
                
            case LEFT_LEG :
                playAsLeftLeg();
                break;
                
            case RIGHT_LEG :
                playAsRightLeg();
                break;
                
            case SERVER_DEBUG :
                playServerDebug();
                break;
                
                default :
                    System.out.println("Player is not defined as a body part - could be a server error.  Application quitting...");
                    System.exit(0);
                    break;
        }
        
        netBehavior = new NetBehavior(conn, interactor, netObjs);
        netBehavior.setSchedulingBounds(bounds);
        this.addChild(netBehavior);
        
        this.compile();
    }
    
    /*
     * Following method loads all the models into the relevant TG's.
     * All the loads are in the same order as the TG's are declared.
     */
    private void loadModels() {
        Inspector3DS loader;
        
        loader = new Inspector3DS("objects/pelvis.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoPelvisTG = loader.getModel();
        
        loader = new Inspector3DS("objects/head.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoHeadTG = loader.getModel();
        
        loader = new Inspector3DS("objects/torso.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoTorsoTG = loader.getModel();
        
        loader = new Inspector3DS("objects/lhand.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLHTG = loader.getModel();
        
        loader = new Inspector3DS("objects/rhand.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRHTG = loader.getModel();
        
        loader = new Inspector3DS("objects/lfoot.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLFTG = loader.getModel();
        
        loader = new Inspector3DS("objects/rfoot.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRFTG = loader.getModel();
        
        loader = new Inspector3DS("objects/lupperleg.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLULTG = loader.getModel();
        
        loader = new Inspector3DS("objects/rupperleg.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRULTG = loader.getModel();
        
        loader = new Inspector3DS("objects/llowerleg.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLLLTG = loader.getModel();
        
        loader = new Inspector3DS("objects/rlowerleg.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRLLTG = loader.getModel();
        
        loader = new Inspector3DS("objects/lupperarm.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLUATG = loader.getModel();
        
        loader = new Inspector3DS("objects/rupperarm.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRUATG = loader.getModel();
        
        loader = new Inspector3DS("objects/llowerarm.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoLLATG = loader.getModel();
        
        loader = new Inspector3DS("objects/rlowerarm.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        apendoRLATG = loader.getModel();
    }
    
    // Sets all TG's capability bits.
    private void setTGCapabilities() {
        apendoPelvisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoHeadTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoTorsoTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLHTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRHTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLULTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRULTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLLLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRLLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLUATG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRUATG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoLLATG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        apendoRLATG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        apendoPelvisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoHeadTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoTorsoTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLHTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRHTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLULTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRULTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLLLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRLLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLUATG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRUATG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoLLATG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        apendoRLATG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }
    
    /*
     * Crucial bit what sets up the players scene graph.  This is pretty nasty looking but very straightforward.
     * All limbs attach at their point of rotation to the attaching limbs TG.  The graph looks like this:
     *
     * this(BG)
     * |-PelvisTG
     *   |-TorsoTG
     *   | |-HeadTG
     *   | |-LeftUpperArmTG
     *   | | |-LeftLowerArmTG
     *   | |   |-LeftHandTG
     *   | |-RightUpperArmTG
     *   |   |-RightLowerArmTG
     *   |     |-RightLowerArmTG
     *   |-LeftUpperLegTG
     *   | |-LeftLowerLegTG
     *   |   |-LeftFootTG
     *   |-RightUpperLegTG
     *     |-RightLowerLegTG
     *       |-RightFootTG
     */
    private void buildSceneGraph() {
        // Add to the root
        this.addChild(apendoPelvisTG);
        
        // Add to pelvis
        apendoPelvisTG.addChild(apendoTorsoTG);
        apendoPelvisTG.addChild(apendoLULTG);
        apendoPelvisTG.addChild(apendoRULTG);
        
        // Add to Torso
        apendoTorsoTG.addChild(apendoHeadTG);
        apendoTorsoTG.addChild(apendoLUATG);
        apendoTorsoTG.addChild(apendoRUATG);
        
        // Construct left arm
        apendoLUATG.addChild(apendoLLATG);
        apendoLLATG.addChild(apendoLHTG);
        
        // Construct right arm
        apendoRUATG.addChild(apendoRLATG);
        apendoRLATG.addChild(apendoRHTG);
        
        // Construct left leg
        apendoLULTG.addChild(apendoLLLTG);
        apendoLLLTG.addChild(apendoLFTG);
        
        // Construct right leg
        apendoRULTG.addChild(apendoRLLTG);
        apendoRLLTG.addChild(apendoRFTG);
    }
    
    /*
     * This method sets the initial locations of all the TG's.
     * I have ordered this according to the scene graph, with exception
     * to the Pelvis, which will be moved last (as the root).
     */
    private void positionTGs() {
        Transform3D trans;
        Vector3f vec;
        
        // The Pelvis
        vec = new Vector3f(0.0f, 176.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoPelvisTG.setTransform(trans);
        
        // The Torso
        vec = new Vector3f(0.0f, 10.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoTorsoTG.setTransform(trans);
        
        // The head
        vec = new Vector3f(0.0f, 110.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoHeadTG.setTransform(trans);
        
        // The left upper arm
        vec = new Vector3f(53.0f, 85.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLUATG.setTransform(trans);
        
        // The left lower arm
        vec = new Vector3f(47.0f, -35.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLLATG.setTransform(trans);
        
        // The left hand
        vec = new Vector3f(30.0f, -60.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLHTG.setTransform(trans);
        
        // The right upper arm
        vec = new Vector3f(-53.0f, 85.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRUATG.setTransform(trans);
        
        // The right lower arm
        vec = new Vector3f(-47.0f, -35.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRLATG.setTransform(trans);
        
        // The right hand
        vec = new Vector3f(-30.0f, -60.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRHTG.setTransform(trans);
        
        // The left upper leg
        vec = new Vector3f(20.0f, -10.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLULTG.setTransform(trans);
        
        // The left lower leg
        vec = new Vector3f(30.0f, -60.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLLLTG.setTransform(trans);
        
        // The left foot
        vec = new Vector3f(10.0f, -85.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoLFTG.setTransform(trans);
        
        // The right upper leg
        vec = new Vector3f(-20.0f, -10.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRULTG.setTransform(trans);
        
        // The right lower leg
        vec = new Vector3f(-30.0f, -60.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRLLTG.setTransform(trans);
        
        // The right foot
        vec = new Vector3f(-10.0f, -85.0f, 0.0f);
        trans = new Transform3D();
        trans.setTranslation(vec);
        apendoRFTG.setTransform(trans);
    }
    
    
    // 5 methods follow for setting up individual players...
    
    private void playAsHead() {
        // Attach the dummy TG to the headTG to get coords for the camera
        TransformGroup dummyTG1 = new TransformGroup();
        apendoHeadTG.addChild(dummyTG1);
        TransformGroup dummyTG2 = new TransformGroup();
        dummyTG2.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        dummyTG1.addChild(dummyTG2);
        
        // Then position it...
        Transform3D transCoord = new Transform3D();
        transCoord.rotY(Math.PI);
        transCoord.setTranslation(new Vector3f(0.0f, 15.0f, 30.0f));
        dummyTG1.setTransform(transCoord);
        
        BehaviorHead behaviorHead = new BehaviorHead(apendoHeadTG, vpTrans, dummyTG2);
        behaviorHead.setSchedulingBounds(bounds);
        this.addChild(behaviorHead);
        
        System.out.println("Playing as Head");
    }
    
    private void playAsLeftHand() {
        // Attach the dummy TG to the headTG to get coords for the camera
        // I needed to use 2 because I'm lazy and it won't really have any performance hit...
        // It means that the coord system used matches the TG dummy is attached to.
        TransformGroup dummyTG1 = new TransformGroup();
        apendoLUATG.addChild(dummyTG1);
        TransformGroup dummyTG2 = new TransformGroup();
        dummyTG2.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        dummyTG1.addChild(dummyTG2);
        
        // Then position it...
        Transform3D transCoord = new Transform3D();
        transCoord.rotY(Math.PI * 1);
        Transform3D transCoord2 = new Transform3D();
        transCoord2.rotX(Math.PI * 1.4);
        transCoord.mul(transCoord2);
        transCoord.setTranslation(new Vector3f(40.0f, 40.0f, 60.0f));
        dummyTG1.setTransform(transCoord);
        
        BehaviorLeftHand behaviorLeftHand = new BehaviorLeftHand(apendoLUATG, apendoLLATG, vpTrans, dummyTG2);
        behaviorLeftHand.setSchedulingBounds(bounds);
        this.addChild(behaviorLeftHand);
        
        System.out.println("Playing as Left Hand");
    }
    
    private void playAsRightHand() {
        TransformGroup dummyTG1 = new TransformGroup();
        apendoRUATG.addChild(dummyTG1);
        TransformGroup dummyTG2 = new TransformGroup();
        dummyTG2.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        dummyTG1.addChild(dummyTG2);
        
        // Then position it...
        Transform3D transCoord = new Transform3D();
        transCoord.rotY(Math.PI * 1);
        Transform3D transCoord2 = new Transform3D();
        transCoord2.rotX(Math.PI * 1.4);
        transCoord.mul(transCoord2);
        transCoord.setTranslation(new Vector3f(-40.0f, 40.0f, 60.0f));
        dummyTG1.setTransform(transCoord);
        
        BehaviorRightHand behaviorRightHand = new BehaviorRightHand(apendoRUATG, apendoRLATG, vpTrans, dummyTG2);
        behaviorRightHand.setSchedulingBounds(bounds);
        this.addChild(behaviorRightHand);
        
        System.out.println("Playing as Right Hand");
    }
    
    private void playAsLeftLeg() {
        TransformGroup dummyTG1 = new TransformGroup();
        apendoPelvisTG.addChild(dummyTG1);
        TransformGroup dummyTG2 = new TransformGroup();
        dummyTG2.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        dummyTG1.addChild(dummyTG2);
        
        // Then position it...
        Transform3D transCoord = new Transform3D();
        transCoord.rotY(Math.PI * 0.5);
        transCoord.setTranslation(new Vector3f(500.0f, -70.0f, 0.0f));
        dummyTG1.setTransform(transCoord);
        
        BehaviorLeftLeg behaviorLeftLeg = new BehaviorLeftLeg(apendoLULTG, apendoLLLTG, vpTrans, dummyTG2);
        behaviorLeftLeg.setSchedulingBounds(bounds);
        this.addChild(behaviorLeftLeg);
        System.out.println("Playing as Left Leg");
    }
    
    private void playAsRightLeg() {
        TransformGroup dummyTG1 = new TransformGroup();
        apendoPelvisTG.addChild(dummyTG1);
        TransformGroup dummyTG2 = new TransformGroup();
        dummyTG2.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        dummyTG1.addChild(dummyTG2);
        
        // Then position it...
        Transform3D transCoord = new Transform3D();
        transCoord.rotY(Math.PI * 1.5);
        transCoord.setTranslation(new Vector3f(-500.0f, -70.0f, 0.0f));
        dummyTG1.setTransform(transCoord);
        
        BehaviorRightLeg behaviorRightLeg = new BehaviorRightLeg(apendoRULTG, apendoRLLTG, vpTrans, dummyTG2);
        behaviorRightLeg.setSchedulingBounds(bounds);
        this.addChild(behaviorRightLeg);
        System.out.println("Playing as Right Leg");
    }
    
    private void playAsServer() {
        // Move the view platform up and back
        Transform3D transCoord = new Transform3D();
        transCoord.setTranslation(new Vector3f(0.0f, 160.0f, 600.0f));
        vpTrans.setTransform(transCoord);
        
        // Add simple key navigation
        com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior keyNavBeh = new com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior(vpTrans);
        keyNavBeh.setSchedulingBounds(bounds);
        this.addChild(keyNavBeh);
        
        BehaviorServer behaviorServer = new BehaviorServer(netObjs, this);
        behaviorServer.setSchedulingBounds(bounds);
        this.addChild(behaviorServer);
        System.out.println("Playing as Server");
    }
    
    /* This is a useful bit of code that allows you to replace the vptrans with a box to make positioning of the camera easier.
     *
        TransformGroup tmp = new TransformGroup();
        tmp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        this.addChild(tmp);
        com.sun.j3d.utils.geometry.ColorCube tmpCube = new com.sun.j3d.utils.geometry.ColorCube(2.0);
        tmp.addChild(tmpCube);
     *
     */
    
    
    /*
     *This special mode of play is specially for debugging the server physics.  The controls of the
     *model all become controlled by the keyboard.
     *
     *This is however NOT a 1 player version of the ApendoMan game.  It's a bit too crap for that
     *(my translation from key to mouse is a bit too crap!!!)
     */
    private void playServerDebug() {
        
        // This is a special class that extends all behaviors and overrides controls...
        ServerDebugControls keyControls = new ServerDebugControls(netObjs);
        this.addChild(keyControls);
        
        playAsServer();
    }
}
