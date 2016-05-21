/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mygame.utils.CustomFunctions;
import mygame.utils.Grid;
import mygame.utils.ParticleManager;

/**
 *
 * @author ARIS CRUZ
 */
public class BulletControl extends AbstractControl {
    
    private int screenWidth, screenHeight;
 
    private float speed = 1100f;
    public Vector3f direction;
    private float rotation;
    private ParticleManager particleManager;
    private Grid grid;
 
    public BulletControl(Vector3f direction, int screenWidth, int screenHeight, 
            ParticleManager particleManager, Grid grid) {
        
        this.direction = direction;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.particleManager = particleManager;
        this.grid = grid;
    }
 
    @Override
    protected void controlUpdate(float tpf) {
        
        //movement
        spatial.move(direction.mult(speed*tpf));
 
        //rotation
        float actualRotation = CustomFunctions.getAngleFromVector(direction);
        if (actualRotation != rotation) {
            spatial.rotate(0,0,actualRotation - rotation);
            rotation = actualRotation;
        }
 
        //check boundaries
        Vector3f loc = spatial.getLocalTranslation();
        if (loc.x > screenWidth || 
            loc.y > screenHeight ||
            loc.x < 0 ||
            loc.y < 0) {
            particleManager.bulletExplosion(loc);
            spatial.removeFromParent();
        } 
        
        grid.applyExplosiveForce(direction.length()*(18f), spatial.getLocalTranslation(), 80);
    }
 
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public void applyGravity(Vector3f gravity) {
        direction.addLocal(gravity);
    }
}