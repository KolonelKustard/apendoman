/*
 * ServerDebugControls.java
 *
 * Created on 21 January 2000, 00:38
 */

package apendoMan.server;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

import apendoMan.apendoBits.*;
import apendoMan.networking.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ServerDebugControls extends BranchGroup {
    private TransformGroup[] allTGs;
    private BoundingSphere bounds;
    
    private TransformGroup dummyCamera;
    private TransformGroup dummyTG;
    
    /** Creates new ServerDebugControls */
    public ServerDebugControls(TransformGroup[] allTGs) {
        this.allTGs = allTGs;
        
        dummyCamera = new TransformGroup();
        dummyCamera.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        this.addChild(dummyCamera);
        
        dummyTG = new TransformGroup();
        dummyTG.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        this.addChild(dummyTG);
        
        bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
        this.setBounds(bounds);
        
        DebugHead debugHead = new DebugHead(allTGs[NetBehavior.HEADTG], dummyCamera, dummyTG);
        debugHead.setSchedulingBounds(bounds);
        this.addChild(debugHead);
        
        DebugLeftHand debugLeftHand = new DebugLeftHand(allTGs[NetBehavior.LEFT_UATG], allTGs[NetBehavior.LEFT_LATG], dummyCamera, dummyTG);
        debugLeftHand.setSchedulingBounds(bounds);
        this.addChild(debugLeftHand);
        
        DebugRightHand debugRightHand = new DebugRightHand(allTGs[NetBehavior.RIGHT_UATG], allTGs[NetBehavior.RIGHT_LATG], dummyCamera, dummyTG);
        debugRightHand.setSchedulingBounds(bounds);
        this.addChild(debugRightHand);
        
        DebugLeftLeg debugLeftLeg = new DebugLeftLeg(allTGs[NetBehavior.LEFT_ULTG], allTGs[NetBehavior.LEFT_LLTG], dummyCamera, dummyTG);
        debugLeftLeg.setSchedulingBounds(bounds);
        this.addChild(debugLeftLeg);
        
        DebugRightLeg debugRightLeg = new DebugRightLeg(allTGs[NetBehavior.RIGHT_ULTG], allTGs[NetBehavior.RIGHT_LLTG], dummyCamera, dummyTG);
        debugRightLeg.setSchedulingBounds(bounds);
        this.addChild(debugRightLeg);
    }

}

// Below come extensions of all the existing behaviors.  The controls are overriden.

class DebugHead extends BehaviorHead {
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    public DebugHead(TransformGroup tg1, TransformGroup tg2, TransformGroup tg3) {
        super(tg1, tg2, tg3);
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        KeyEvent evt;
        
        x = y = 0;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof KeyEvent) {
                        evt = (KeyEvent) events[num];
                        
                        if (evt.getKeyCode() == KeyEvent.VK_W) {
                            y -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_S) {
                            y += 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_A) {
                            x -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_D) {
                            x += 50;
                        }
                    }
                }
            }
        }
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
}

class DebugLeftHand extends BehaviorLeftHand {
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    public DebugLeftHand(TransformGroup tg1, TransformGroup tg2, TransformGroup tg3, TransformGroup tg4) {
        super(tg1, tg2, tg3, tg4);
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        KeyEvent evt;
        
        mouseX = mouseY = 0;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof KeyEvent) {
                        evt = (KeyEvent) events[num];
                        
                        if (evt.getKeyCode() == KeyEvent.VK_T) {
                            mouseY -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_G) {
                            mouseY += 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_F) {
                            mouseX -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_H) {
                            mouseX += 50;
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_Y) {
                                elbowChanging = true;
                                elbowForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_R) {
                                elbowChanging = true;
                                elbowForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((evt.getKeyCode() == KeyEvent.VK_R) || (evt.getKeyCode() == KeyEvent.VK_Y)) {
                                elbowChanging = false;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
}

class DebugRightHand extends BehaviorRightHand {
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    public DebugRightHand(TransformGroup tg1, TransformGroup tg2, TransformGroup tg3, TransformGroup tg4) {
        super(tg1, tg2, tg3, tg4);
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        KeyEvent evt;
        
        mouseX = mouseY = 0;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof KeyEvent) {
                        evt = (KeyEvent) events[num];
                        
                        if (evt.getKeyCode() == KeyEvent.VK_K) {
                            mouseY += 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_I) {
                            mouseY -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_J) {
                            mouseX -= 50;
                        }
                        if (evt.getKeyCode() == KeyEvent.VK_L) {
                            mouseX += 50;
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_O) {
                                elbowChanging = true;
                                elbowForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_U) {
                                elbowChanging = true;
                                elbowForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((evt.getKeyCode() == KeyEvent.VK_O) || (evt.getKeyCode() == KeyEvent.VK_U)) {
                                elbowChanging = false;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
}

class DebugLeftLeg extends BehaviorLeftLeg {
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    public DebugLeftLeg(TransformGroup tg1, TransformGroup tg2, TransformGroup tg3, TransformGroup tg4) {
        super(tg1, tg2, tg3, tg4);
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        KeyEvent evt;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof KeyEvent) {
                        evt = (KeyEvent) events[num];
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_X) {
                                lulChanging = true;
                                lulForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_Z) {
                                lulChanging = true;
                                lulForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_V) {
                                lllChanging = true;
                                lllForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_C) {
                                lllChanging = true;
                                lllForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((evt.getKeyCode() == KeyEvent.VK_X) || (evt.getKeyCode() == KeyEvent.VK_Z))
                                lulChanging = false;
                            
                            if ((evt.getKeyCode() == KeyEvent.VK_V) || (evt.getKeyCode() == KeyEvent.VK_C))
                                lllChanging = false;
                        }
                    }
                }
            }
        }
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
}

class DebugRightLeg extends BehaviorRightLeg {
    
    WakeupCriterion criterion[] = {new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
                                   new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED),
                                   new WakeupOnElapsedTime(10)};
    WakeupCondition conditions = new WakeupOr( criterion );
    
    public DebugRightLeg(TransformGroup tg1, TransformGroup tg2, TransformGroup tg3, TransformGroup tg4) {
        super(tg1, tg2, tg3, tg4);
    }
    
    protected void sortInput(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        KeyEvent evt;
        
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for (int num = 0; num < events.length; num++) {
                    if (events[num] instanceof KeyEvent) {
                        evt = (KeyEvent) events[num];
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_N) {
                                rulChanging = true;
                                rulForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_B) {
                                rulChanging = true;
                                rulForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_PRESSED) {
                            if (evt.getKeyCode() == KeyEvent.VK_COMMA) {
                                rllChanging = true;
                                rllForward = true;
                            }
                            else if (evt.getKeyCode() == KeyEvent.VK_M) {
                                rllChanging = true;
                                rllForward = false;
                            }
                        }
                        
                        if (evt.getID() == KeyEvent.KEY_RELEASED) {
                            if ((evt.getKeyCode() == KeyEvent.VK_N) || (evt.getKeyCode() == KeyEvent.VK_B))
                                rulChanging = false;
                            
                            if ((evt.getKeyCode() == KeyEvent.VK_COMMA) || (evt.getKeyCode() == KeyEvent.VK_M))
                                rllChanging = false;
                        }
                    }
                }
            }
        }
    }
    
    public void processStimulus(Enumeration criteria) {
        sortInput(criteria);
        changeTGs();
        this.wakeupOn(conditions);
    }
}