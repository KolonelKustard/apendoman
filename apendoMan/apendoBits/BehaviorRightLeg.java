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
public class BehaviorRightLeg extends Behavior {
    private final float RUL_MAX_LIMIT = 1.5f;
    private final float RUL_MIN_LIMIT = -1.1f;
    private final float RLL_MAX_LIMIT = 0.0f;
    private final float RLL_MIN_LIMIT = -2.0f;
    private final float RUL_FACTOR = 0.001f;
    private final float RLL_FACTOR = 0.001f;
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    
    WakeupCondition conditions = new WakeupOr( criterion );
    
    // The two controlled apendo TGs...
    private TransformGroup rulTG, rllTG;
    // The controlled camera and related dummy TGs...
    private TransformGroup vpTG, dummyTG;
    
    // The 2 key controlled elements of...
    private int rulFlex, rllFlex;
    // The 2 angles to apply to the joints
    private float rul_angle, rll_angle;
    
    private Transform3D currentTrans;
    
    protected boolean rulChanging = false;
    protected boolean rllChanging = false;
    protected boolean rulForward = true;
    protected boolean rllForward = true;

    /** Creates new BehaviorLeftHand */
    public BehaviorRightLeg(TransformGroup rulTG, TransformGroup rllTG, TransformGroup vpTG, TransformGroup dummyTG) {
        this.rulTG = rulTG;
        this.rllTG = rllTG;
        this.vpTG = vpTG;
        this.dummyTG = dummyTG;
        
        rulFlex = rllFlex = 0;
        rul_angle = rll_angle = 0.0f;
        
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
                    if (events[num] instanceof KeyEvent) {
                        kEvt = (KeyEvent) events[num];
                        
                        if (kEvt.getID() == KeyEvent.KEY_PRESSED) {
                            if (kEvt.getKeyCode() == KeyEvent.VK_Q) {
                                rulChanging = true;
                                rulForward = true;
                            }
                            else if (kEvt.getKeyCode() == KeyEvent.VK_A) {
                                rulChanging = true;
                                rulForward = false;
                            }
                        }
                        
                        if (kEvt.getID() == KeyEvent.KEY_PRESSED) {
                            if (kEvt.getKeyCode() == KeyEvent.VK_W) {
                                rllChanging = true;
                                rllForward = true;
                            }
                            else if (kEvt.getKeyCode() == KeyEvent.VK_S) {
                                rllChanging = true;
                                rllForward = false;
                            }
                        }
                        
                        if (kEvt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((kEvt.getKeyCode() == KeyEvent.VK_Q) || (kEvt.getKeyCode() == KeyEvent.VK_A))
                                rulChanging = false;
                            
                            if ((kEvt.getKeyCode() == KeyEvent.VK_W) || (kEvt.getKeyCode() == KeyEvent.VK_S))
                                rllChanging = false;
                        }
                        
                    }
                }
            }
        }
    }
    
    protected void changeTGs() {
        if (rulChanging) {
            if (rulForward) {
                if (rulFlex < 0) rulFlex += 2;
                else rulFlex ++;
            }
            else {
                if (rulFlex > 0) rulFlex -= 2;
                else rulFlex --;
            }
        }
        else {
            if (rulFlex < 0)
                rulFlex ++;
            else {
                if (rulFlex > 0)
                    rulFlex --;
            }
        }
        
        if (rllChanging) {
            if (rllForward) {
                if (rllFlex < 0) rllFlex += 2;
                else rllFlex ++;
            }
            else {
                if (rllFlex > 0) rllFlex -= 2;
                else rllFlex --;
            }
        }
        else {
            if (rllFlex < 0)
                rllFlex ++;
            else {
                if (rllFlex > 0)
                    rllFlex --;
            }
        }
        
        rul_angle += rulFlex * RUL_FACTOR;
        rll_angle += rllFlex * RLL_FACTOR;
        
        if (rul_angle >= RUL_MAX_LIMIT) {
            rul_angle = RUL_MAX_LIMIT;
            rulFlex = 0;
        }
        if (rul_angle <= RUL_MIN_LIMIT) {
            rul_angle = RUL_MIN_LIMIT;
            rulFlex = 0;
        }
        
        if (rll_angle >= RLL_MAX_LIMIT) {
            rll_angle = RLL_MAX_LIMIT;
            rllFlex = 0;
        }
        if (rll_angle <= RLL_MIN_LIMIT) {
            rll_angle = RLL_MIN_LIMIT;
            rllFlex = 0;
        }
        
        // Process for upper leg
        Transform3D rulTrans = new Transform3D();
        rulTrans.rotX(0 - rul_angle);
        Vector3f rulVec = new Vector3f();
        rulTG.getTransform(currentTrans);
        currentTrans.get(rulVec);
        rulTrans.setTranslation(rulVec);
        rulTG.setTransform(rulTrans);
        
        // Process for lower leg
        Transform3D rllTrans = new Transform3D();
        rllTrans.rotX(0 - rll_angle);
        Vector3f rllVec = new Vector3f();
        rllTG.getTransform(currentTrans);
        currentTrans.get(rllVec);
        rllTrans.setTranslation(rllVec);
        rllTG.setTransform(rllTrans);
        
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
