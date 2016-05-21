/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author ARIS CRUZ
 */
public class Assets extends SimpleApplication {
    
    public static void main(String[] args) {
        Assets assets = new Assets();
        assets.start();
    }

    @Override
    public void simpleInitApp() {
        
        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material materialDefault = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(materialDefault);
        rootNode.attachChild(teapot);
        
        Box box = new Box(2.5f,2.5f,1.0f);
        Spatial wall = new Geometry("Box", box);
        Material materialBrick = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        materialBrick.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(materialBrick);
        wall.setLocalTranslation(2.0f,-2.5f,0.0f);
        
        rootNode.attachChild(wall);
        
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bitmapText = new BitmapText(guiFont, false);
        bitmapText.setSize(guiFont.getCharSet().getRenderedSize());
        bitmapText.setText("Hello World Fuckers!");
        bitmapText.setLocalTranslation(300, bitmapText.getLineHeight(), 0);
        guiNode.attachChild(bitmapText);
        
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.05f, 0.05f, 0.05f);
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, -5.0f, -2.0f);
        rootNode.attachChild(ninja);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
    
    }   
    
}
