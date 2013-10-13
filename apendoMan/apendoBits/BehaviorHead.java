/*
 * BehaviorLeftHand.java
 *
 * Created on 21 November 2000, 19:14
 */

package apendoMan.apendoBits;

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
public class BehaviorHead extends Behavior {
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 480;
    private final float Y_MAX_LIMIT = 0.3f;
    private final float Y_MIN_LIMIT = -0.9f;
    private final float X_FACTOR = 0.0001f;
    private final float Y_FACTOR = 0.0001f;
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    private TransformGroup headtg;
    private TransformGroup vpTrans;
    private TransformGroup dummyTrans;
    private Transform3D currentTrans;
    
    private Robot robot;
    
    protected int x, y;
    private float x_angle, y_angle;
    
    private float angle;
    
    /** Creates new BehaviorLeftHand */
    public BehaviorHead(TransformGroup headtg, TransformGroup vpTrans, TransformGroup dummyTrans) {
        this.headtg = headtg;
        this.vpTrans = vpTrans;
        this.dummyTrans = dummyTrans;
        x = y = 0;
        x_angle = 0.0f;
        y_angle = 0.0f;
        
        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            System.out.println("No ability to set current mouse location.  Movement will be limited to when pointer is within confines of game window");
            e.printStackTrace();
        }
        
        currentTrans = new Transform3D();
    }
    
    public void initialize() {
        // set initial wakeup condition
        this.wakeupOn(new WakeupOnElapsedFrames(0));
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        MouseEvent evt;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                if (events.length > 0) {
                    evt = (MouseEvent) events[events.length-1];
                    
                    x = (evt.getX() - (SCREEN_WIDTH / 2));
                    y = (evt.getY() - (SCREEN_HEIGHT / 2));
                }
            }
        }
    }
    
    protected void changeTGs() {
        // First sets the new X and Y angles...
        x_angle += x * X_FACTOR;
        y_angle += y * Y_FACTOR;
        
        // Then set them  to min or max if they are out of range...
        if (y_angle <= Y_MIN_LIMIT) {
            y_angle = Y_MIN_LIMIT;
        }
        if (y_angle >= Y_MAX_LIMIT) {
            y_angle = Y_MAX_LIMIT;
        }
        
        Transform3D transX = new Transform3D();
        transX.rotY(0 - x_angle);
        
        Transform3D transY = new Transform3D();
        transY.rotX(y_angle);
        
        Transform3D trans = new Transform3D();
        trans.mul(transX);
        trans.mul(transY);
        
        // Need to get coords to put tg back again after rotate...
        Vector3f vec = new Vector3f();
        headtg.getTransform(currentTrans);
        currentTrans.get(vec);
        
        // Now set our new trans3d to the correct coords...
        trans.setTranslation(vec);
        
        // then set our TG to the new trans3d
        headtg.setTransform(trans);
        
        // and finally...  set the viewing platform TG to the invisible TG attached to the head...
        Transform3D transV = new Transform3D();
        dummyTrans.getLocalToVworld(transV);
        vpTrans.setTransform(transV);
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
    
}
