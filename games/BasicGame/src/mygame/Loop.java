/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author ARIS CRUZ
 */
public class Loop extends SimpleApplication {

    protected Geometry player, player2;
    protected Material mat1, mat2;
    protected int i = 0;
    
    public static void main(String[] args) {
        Loop loop = new Loop();
        loop.start();
    }
    
    @Override
    public void simpleInitApp() {
       
        Box b = new Box(1, 1, 1);
        player = new Geometry("Player", b);
        player.setLocalTranslation(new Vector3f(1,-1,1));
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat1);
        
        Box b2 = new Box(1, 1, 1);
        player2 = new Geometry("Player 2", b2);
        player2.setLocalTranslation(new Vector3f(5,-1,1));
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        player2.setMaterial(mat2);
        
        rootNode.attachChild(player);  
        rootNode.attachChild(player2); 
    }   
    
    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:

        if (i%2 == 0) {
            player.rotate(0, 2*tpf, 0); 
            player2.rotate(3*tpf, 0 , 0);
        } else {
            mat1.setColor("Color", ColorRGBA.Red);
            player.setMaterial(mat1);
            player.rotate(0, 2*tpf, 0);

            mat2.setColor("Color", ColorRGBA.Blue);
            player2.setMaterial(mat2);
            player2.rotate(3*tpf, 0 , 0);
        }
        i++;
      }
}
