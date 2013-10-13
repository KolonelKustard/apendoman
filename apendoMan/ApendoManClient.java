/*
 * apendoManClient.java
 *
 * Created on 28 October 2000, 15:55
 */

package apendoMan;

import java.awt.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import apendoMan.networking.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ApendoManClient {
    
    private Frame parent;
    private Frame window;
    private Canvas3D canvas;
    private TransformGroup vpTrans = null;
    
    private SceneryLoader scenery;
    private ApendoPlayer apendoPlayer1;

    /** Creates new apendoManClient */
    public ApendoManClient(Frame parent, ConnectToServer conn, int interactor) {
        this.parent = parent;
        
        window = new Frame();
        
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);

        canvas = new Canvas3D(gc);
        
        BranchGroup objRoot = new BranchGroup();
        
        // Start of making stuff!...
        
        SimpleUniverse u = new SimpleUniverse(canvas);
        
        View view = u.getViewer().getView();
        view.getPhysicalEnvironment().setSensorCount(100);
        view.setBackClipDistance(100000f);

        // Get viewing platform TG
        ViewingPlatform vp = u.getViewingPlatform();
        vpTrans = vp.getViewPlatformTransform();
        
        // Begin by making a ground for Apendo (will progress to become scenery hopefully!)
        scenery = new SceneryLoader();
        objRoot.addChild(scenery);
        
        // Now construct ApendoMan
        apendoPlayer1 = new ApendoPlayer(conn, interactor, vpTrans);
        objRoot.addChild(apendoPlayer1);
        
        //objRoot.compile();
        u.addBranchGraph(objRoot);
        
        window.add("Center", canvas);
        window.setSize(640, 480);
	window.setVisible(true);
        window.transferFocus();
    }
    
    public static void main (String args[]) {
        Frame trev = new Frame("ApendoBeta (v0.1) To quit, close this window.");
        
        trev.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        }
        );
        
        Label label = new Label();
        label.setAlignment(Label.CENTER);
        trev.add(label);
        
        trev.setSize(640, 60);
        trev.setLocation(0, 480);
        trev.show();
        
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("h")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.HEAD);
                label.setText("Playing as head.  Controls are mouse to look");
            }
            else if (args[0].equalsIgnoreCase("lh")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.LEFT_HAND);
                label.setText("Playing as left arm/hand.  Controls are mouse to rotate shoulder joint and Q and A to flex elbow.");
            }
            else if (args[0].equalsIgnoreCase("rh")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.RIGHT_HAND);
                label.setText("Playing as right arm/hand.  Controls are mouse to rotate shoulder joint and Q and A to flex elbow.");
            }
            else if (args[0].equalsIgnoreCase("lf")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.LEFT_LEG);
                label.setText("Playing as left leg/foot.  Controls are Q and A to flex at hip, and W and S to flex knee.");
            }
            else if (args[0].equalsIgnoreCase("rf")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.RIGHT_LEG);
                label.setText("Playing as right leg/foot.  Controls are Q and A to flex at hip, and W and S to flex knee.");
            }
            else if (args[0].equalsIgnoreCase("1play")) {
                new ApendoManClient(trev, new ConnectToServer("NULL"), ApendoPlayer.SERVER_DEBUG);
                label.setText("Playing in 1 player server debug mode.  See console for controls.");
                System.out.println("Keys");
                System.out.println("====");
                System.out.println("");
                System.out.println("Head:      W, A, S, D");
                System.out.println("Left Arm:  T, F, G, H, R, Y");
                System.out.println("Right Arm: I, J, K, L, U, O");
                System.out.println("Left Leg:  Z, X, C, V");
                System.out.println("Right Leg  B, N, M, ,");
            }
        }
        else {
            label.setText("You needed to start the app with an argument.  h - head, lh - left hand, rh - right hand, lf - left foot, rf - right foot");
            trev.repaint();
        }
    }
    
}
