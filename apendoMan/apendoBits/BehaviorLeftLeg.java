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
public class BehaviorLeftLeg extends Behavior {
    private final float LUL_MAX_LIMIT = 1.5f;
    private final float LUL_MIN_LIMIT = -1.1f;
    private final float LLL_MAX_LIMIT = 0.0f;
    private final float LLL_MIN_LIMIT = -2.0f;
    private final float LUL_FACTOR = 0.001f;
    private final float LLL_FACTOR = 0.001f;
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    
    WakeupCondition conditions = new WakeupOr( criterion );
    
    // The two controlled apendo TGs...
    private TransformGroup lulTG, lllTG;
    // The controlled camera and related dummy TGs...
    private TransformGroup vpTG, dummyTG;
    
    // The 2 key controlled elements of...
    private int lulFlex, lllFlex;
    // The 2 angles to apply to the joints
    private float lul_angle, lll_angle;
    
    private Transform3D currentTrans;
    
    protected boolean lulChanging = false;
    protected boolean lllChanging = false;
    protected boolean lulForward = true;
    protected boolean lllForward = true;

    /** Creates new BehaviorLeftHand */
    public BehaviorLeftLeg(TransformGroup lulTG, TransformGroup lllTG, TransformGroup vpTG, TransformGroup dummyTG) {
        this.lulTG = lulTG;
        this.lllTG = lllTG;
        this.vpTG = vpTG;
        this.dummyTG = dummyTG;
        
        lulFlex = lllFlex = 0;
        lul_angle = lll_angle = 0.0f;
        
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
                                lulChanging = true;
                                lulForward = true;
                            }
                            else if (kEvt.getKeyCode() == KeyEvent.VK_A) {
                                lulChanging = true;
                                lulForward = false;
                            }
                        }
                        
                        if (kEvt.getID() == KeyEvent.KEY_PRESSED) {
                            if (kEvt.getKeyCode() == KeyEvent.VK_W) {
                                lllChanging = true;
                                lllForward = true;
                            }
                            else if (kEvt.getKeyCode() == KeyEvent.VK_S) {
                                lllChanging = true;
                                lllForward = false;
                            }
                        }
                        
                        if (kEvt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((kEvt.getKeyCode() == KeyEvent.VK_Q) || (kEvt.getKeyCode() == KeyEvent.VK_A))
                                lulChanging = false;
                            
                            if ((kEvt.getKeyCode() == KeyEvent.VK_W) || (kEvt.getKeyCode() == KeyEvent.VK_S))
                                lllChanging = false;
                        }
                        
                    }
                }
            }
        }
    }
    
    protected void changeTGs() {
        if (lulChanging) {
            if (lulForward) {
                if (lulFlex < 0) lulFlex += 2;
                else lulFlex ++;
            }
            else {
                if (lulFlex > 0) lulFlex -= 2;
                else lulFlex --;
            }
        }
        else {
            if (lulFlex < 0)
                lulFlex ++;
            else {
                if (lulFlex > 0)
                    lulFlex --;
            }
        }
        
        if (lllChanging) {
            if (lllForward) {
                if (lllFlex < 0) lllFlex += 2;
                else lllFlex ++;
            }
            else {
                if (lllFlex > 0) lllFlex -= 2;
                else lllFlex --;
            }
        }
        else {
            if (lllFlex < 0)
                lllFlex ++;
            else {
                if (lllFlex > 0)
                    lllFlex --;
            }
        }
        
        lul_angle += lulFlex * LUL_FACTOR;
        lll_angle += lllFlex * LLL_FACTOR;
        
        if (lul_angle >= LUL_MAX_LIMIT) {
            lul_angle = LUL_MAX_LIMIT;
            lulFlex = 0;
        }
        if (lul_angle <= LUL_MIN_LIMIT) {
            lul_angle = LUL_MIN_LIMIT;
            lulFlex = 0;
        }
        
        if (lll_angle >= LLL_MAX_LIMIT) {
            lll_angle = LLL_MAX_LIMIT;
            lllFlex = 0;
        }
        if (lll_angle <= LLL_MIN_LIMIT) {
            lll_angle = LLL_MIN_LIMIT;
            lllFlex = 0;
        }
        
        // Process for upper leg
        Transform3D lulTrans = new Transform3D();
        lulTrans.rotX(0 - lul_angle);
        Vector3f lulVec = new Vector3f();
        lulTG.getTransform(currentTrans);
        currentTrans.get(lulVec);
        lulTrans.setTranslation(lulVec);
        lulTG.setTransform(lulTrans);
        
        // Process for lower leg
        Transform3D lllTrans = new Transform3D();
        lllTrans.rotX(0 - lll_angle);
        Vector3f lllVec = new Vector3f();
        lllTG.getTransform(currentTrans);
        currentTrans.get(lllVec);
        lllTrans.setTranslation(lllVec);
        lllTG.setTransform(lllTrans);
        
        // Now change the camera...
        Transform3D transV = new Transform3D();
        dummyTG.getLocalToVworld(transV);
        vpTG.setTransform(transV);
    }
    
    public void processStimulus(Enumeration criteria){
        sortInput (criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }

}
