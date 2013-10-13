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
public class BehaviorLeftHand extends Behavior {
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 480;
    private final float Y_MIN_LIMIT = -1.3f;
    private final float Y_MAX_LIMIT = 0.8f;
    private final float ELBOW_MAX_LIMIT = 0.0f;
    private final float ELBOW_MIN_LIMIT = -1.2f;
    private final float X_FACTOR = 0.0001f;
    private final float Y_FACTOR = 0.0001f;
    private final float ELBOW_FACTOR = 0.001f;
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED),
                                   new WakeupOnElapsedTime(10)};
    
    WakeupCondition conditions = new WakeupOr(criterion);
    
    // This behavior also controls the main viewing platform (seemed to make sense to keep them tied closely... - may need to change this later)
    private TransformGroup vpTG;
    private TransformGroup dummyTG;
    
    private TransformGroup lupperarmTG;
    private TransformGroup llowerarmTG;
    private Transform3D currentTrans;
    
    protected boolean elbowChanging = false;
    protected boolean elbowForward = true;
    protected int mouseX, mouseY, elbowFlex;
    private float y_angle, x_angle, elbow_angle;

    /** Creates new BehaviorLeftHand */
    public BehaviorLeftHand(TransformGroup lupperarmTG, TransformGroup llowerarmTG, TransformGroup vpTG, TransformGroup dummyTG) {
        this.lupperarmTG = lupperarmTG;
        this.llowerarmTG = llowerarmTG;
        this.vpTG = vpTG;
        this.dummyTG = dummyTG;
        
        mouseX = mouseY = elbowFlex = 0;
        y_angle = 0.0f;
        x_angle = 0.0f;
        elbow_angle = 0.0f;
        
        currentTrans = new Transform3D();
    }
    
    public void initialize(){
        // set initial wakeup condition
        this.wakeupOn(new WakeupOnElapsedFrames(0));
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        MouseEvent mEvt;
        KeyEvent kEvt;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof MouseEvent) {
                        mEvt = (MouseEvent) events[num];
                        
                        mouseX = (mEvt.getX() - (SCREEN_WIDTH / 2));
                        mouseY = (mEvt.getY() - (SCREEN_HEIGHT / 2));
                    }
                    
                    if (events[num] instanceof KeyEvent) {
                        kEvt = (KeyEvent) events[num];
                        
                        if (kEvt.getID() == KeyEvent.KEY_PRESSED) {
                            if (kEvt.getKeyCode() == KeyEvent.VK_Q) {
                                elbowChanging = true;
                                elbowForward = true;
                            }
                            else if (kEvt.getKeyCode() == KeyEvent.VK_A) {
                                elbowChanging = true;
                                elbowForward = false;
                            }
                        }
                        
                        if (kEvt.getID() == KeyEvent.KEY_RELEASED) {
                            elbowChanging = false;
                        }
                    }
                }
            }
        }
    }
    
    protected void changeTGs() {
        if (elbowChanging) {
            if (elbowForward) {
                if (elbowFlex < 0) elbowFlex += 2;
                else elbowFlex ++;
            }
            else {
                if (elbowFlex > 0) elbowFlex -= 2;
                else elbowFlex --;
            }
        }
        else {
            if (elbowFlex < 0)
                elbowFlex ++;
            else {
                if (elbowFlex > 0)
                    elbowFlex --;
            }
        }

        y_angle += mouseX * Y_FACTOR;
        x_angle += mouseY * X_FACTOR;
        elbow_angle -= elbowFlex * ELBOW_FACTOR;
        
        if (y_angle <= Y_MIN_LIMIT) {
            y_angle = Y_MIN_LIMIT;
        }
        if (y_angle >= Y_MAX_LIMIT) {
            y_angle = Y_MAX_LIMIT;
        }
        
        if (elbow_angle <= ELBOW_MIN_LIMIT) {
            elbow_angle = ELBOW_MIN_LIMIT;
            elbowFlex = 0;
        }
        if (elbow_angle >= ELBOW_MAX_LIMIT) {
            elbow_angle = ELBOW_MAX_LIMIT;
            elbowFlex = 0;
        }
        
        Transform3D transX = new Transform3D();
        transX.rotX(x_angle);
        
        Transform3D transY = new Transform3D();
        transY.rotY(0 - y_angle);
        
        Transform3D trans = new Transform3D();
        trans.mul(transY);
        trans.mul(transX);
        
        // Need to get coords to put tg back again after rotate...
        Vector3f vec = new Vector3f();
        lupperarmTG.getTransform(currentTrans);
        currentTrans.get(vec);
        
        // Now set our new trans3d to the correct coords...
        trans.setTranslation(vec);
        
        // then set our TG to the new trans3d
        lupperarmTG.setTransform(trans);
        
        // Then repeat process for lower arm
        Transform3D elbowTrans = new Transform3D();
        elbowTrans.rotZ(elbow_angle);
        Vector3f elbowVec = new Vector3f();
        llowerarmTG.getTransform(currentTrans);
        currentTrans.get(elbowVec);
        elbowTrans.setTranslation(elbowVec);
        llowerarmTG.setTransform(elbowTrans);
        
        // Now change the camera...
        Transform3D transV = new Transform3D();
        dummyTG.getLocalToVworld(transV);
        vpTG.setTransform(transV);
    }
    
    public void processStimulus(Enumeration criteria){
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
    
}
