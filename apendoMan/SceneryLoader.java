/*
 * SceneryLoader.java
 *
 * Created on 30 October 2000, 20:45
 */


package apendoMan;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.*;

import com.mnstarfire.loaders3d.Inspector3DS;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class SceneryLoader extends BranchGroup {
    private final int LOADER_DEBUG = 0;
    private final boolean SHOW_LIGHTS = false;
    
    private TransformGroup[] light = new TransformGroup[6];

    /** Creates new SceneryLoader */
    public SceneryLoader() {
        this.addChild(getScenery());
        this.addChild(getLights());
        this.compile();
    }
    
    private TransformGroup getScenery() {
        TransformGroup scenery;
        Inspector3DS loader;
        
        loader = new Inspector3DS("objects/stadium.3ds");
        loader.setDetail(LOADER_DEBUG);
        loader.parseIt();
        scenery = loader.getModel();
        
        return scenery;
    }
    
    private TransformGroup getLights() {
        TransformGroup lightTGs;
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
        Point3f point = new Point3f(0.0f, 0.0f, 0.0f);
        Transform3D trans;
        
        lightTGs = new TransformGroup();
        
        for (int num = 0; num < light.length; num++) {
            light[num] = new TransformGroup();
        }
        
        // Add light 1 (Main light from above)
        PointLight light1 = new PointLight();
        light[0].addChild(light1);
        if (SHOW_LIGHTS) light[0].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        //light1.setSpreadAngle(1.0f);
        light1.setAttenuation(1.6f, 0.0f, 0.0f);
        light1.setColor(new Color3f(1.0f, 1.0f, 1.0f));
	light1.setInfluencingBounds(bounds);
        
        // Position light 1's TG
        trans = new Transform3D();
        //trans.rotX(Math.PI * 1.5);
        trans.setTranslation(new Vector3f(0.0f, 10000.0f, 0.0f));
        light[0].setTransform(trans);
        
        
        // Add light 2 (left archway)
        SpotLight light2 = new SpotLight();
        light[1].addChild(light2);
        if (SHOW_LIGHTS) light[1].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light2.setSpreadAngle(0.6f);
        light2.setAttenuation(0.5f, 0.0f, 0.0f);
        light2.setColor(new Color3f(0.6f, 0.8f, 1.0f));
	light2.setInfluencingBounds(bounds);
        
        // Position light 2's TG
        trans = new Transform3D();
        trans.rotX(1.2);
        trans.setTranslation(new Vector3f(-50.0f, 0.0f, -1800.0f));
        light[1].setTransform(trans);
        
        
        // Add light 3 (right archway)
        SpotLight light3 = new SpotLight();
        light[2].addChild(light3);
        if (SHOW_LIGHTS) light[2].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light3.setSpreadAngle(0.6f);
        light3.setAttenuation(0.5f, 0.0f, 0.0f);
        light3.setColor(new Color3f(0.6f, 0.8f, 1.0f));
	light3.setInfluencingBounds(bounds);
        
        // Position light 3's TG
        trans = new Transform3D();
        trans.rotX(1.2);
        trans.setTranslation(new Vector3f(800.0f, 0.0f, -1800.0f));
        light[2].setTransform(trans);
        
        
        // Add light 4 (right archway)
        SpotLight light4 = new SpotLight();
        light[3].addChild(light4);
        if (SHOW_LIGHTS) light[3].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light4.setSpreadAngle(0.6f);
        light4.setAttenuation(0.5f, 0.0f, 0.0f);
        light4.setColor(new Color3f(1.0f, 1.0f, 0.9f));
	light4.setInfluencingBounds(bounds);
        
        // Position light 4's TG
        trans = new Transform3D();
        trans.rotX(2.8);
        trans.setTranslation(new Vector3f(0.0f, 0.0f, 800.0f));
        light[3].setTransform(trans);
        
        
        // Add light 5 (lights up front of apendoman)
        SpotLight light5 = new SpotLight();
        light[4].addChild(light5);
        if (SHOW_LIGHTS) light[4].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light5.setSpreadAngle(1.0f);
        light5.setAttenuation(0.005f, 0.005f, 0.0f);
        light5.setColor(new Color3f(1.0f, 1.0f, 1.0f));
	light5.setInfluencingBounds(bounds);
        
        // Position light 5's TG
        trans = new Transform3D();
        trans.setTranslation(new Vector3f(0.0f, 180.0f, 400.0f));
        light[4].setTransform(trans);
        
        
        // Add light 6 (lights up back of apendoman)
        SpotLight light6 = new SpotLight();
        light[5].addChild(light6);
        if (SHOW_LIGHTS) light[5].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light6.setSpreadAngle(0.5f);
        light6.setAttenuation(2.5f, 0.0f, 0.0f);
        light6.setColor(new Color3f(1.0f, 1.0f, 1.0f));
	light6.setInfluencingBounds(bounds);
        
        // Position light 6's TG
        trans = new Transform3D();
        trans.rotY(Math.PI);
        trans.setTranslation(new Vector3f(0.0f, 180.0f, -400.0f));
        light[5].setTransform(trans);
        
        
        /*
        AmbientLight al = new AmbientLight(true, new Color3f(1.0f ,1.0f ,1.0f));
	al.setInfluencingBounds(bounds);
         */

        for (int num = 0; num < light.length; num++) {
	    lightTGs.addChild(light[num]);
        }
        
        return lightTGs;
    }

}

/*
// Add light 1 (In archway)
        PointLight light1 = new PointLight();
        light[0].addChild(light1);
        light[0].addChild(new com.sun.j3d.utils.geometry.ColorCube(10.0));
        
        light1.setAttenuation(1.3f, 0.0f, 0.0f);
        light1.setColor(new Color3f(1.0f, 1.0f, 1.0f));
	light1.setInfluencingBounds(bounds);
        
        // Position light 1's TG
        trans = new Transform3D();
        trans.setTranslation(new Vector3f(0.0f, 800.0f, 0.0f));
        light[0].setTransform(trans);
 */