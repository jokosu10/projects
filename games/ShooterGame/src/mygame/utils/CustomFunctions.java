/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.utils;

import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.Random;
import mygame.BlasterMain;
import mygame.controls.BlackHoleControl;
import mygame.controls.BulletControl;
import mygame.controls.ParticleControl;
import mygame.controls.PlayerControl;
import mygame.controls.SeekerControl;
import mygame.controls.WandererControl;

/**
 *
 * @author ARIS CRUZ
 */
public class CustomFunctions {
    
    private InputManager inputManager;
    private Spatial player;
    private Node bulletNode,enemyNode, blackHoleNode, particleNode;
    private long enemySpawnCooldown, blackHoleSpawnCooldown;
    private float enemySpawnChance;
    private AppSettings settings;
    private BlasterMain blasterMain;
    private Sound sound;
    private Hud hud;
    private ParticleManager particleManager;
    private Grid grid;
    
    public CustomFunctions(InputManager inputManager, Spatial player,
                 Node bulletNode, Node enemyNode, Node blackHoleNode, Node particleNode,
                 long enemySpawnCooldown,long blackHoleSpawnCooldown, float enemySpawnChance, 
                 AppSettings appsettings, Sound sound, Hud hud, ParticleManager particleManager,
                 Grid grid, BlasterMain blasterMain) {
        
        this.player = player;
        this.inputManager = inputManager;
        this.bulletNode = bulletNode;
        this.enemyNode = enemyNode;
        this.blackHoleNode = blackHoleNode;
        this.particleNode = particleNode;
        this.enemySpawnChance = enemySpawnChance;
        this.enemySpawnCooldown = enemySpawnCooldown;
        this.blackHoleSpawnCooldown = blackHoleSpawnCooldown;
        this.sound = sound;
        this.hud = hud;
        this.particleManager = particleManager;
        this.grid = grid;
        this.blasterMain = blasterMain;
        this.settings = appsettings;
    }
     
     public void createSeeker() {
        Spatial seeker = blasterMain.getSpatial("Seeker");
        seeker.setLocalTranslation(getSpawnPosition());
        seeker.addControl(new SeekerControl(player));
        seeker.setUserData("active",false);
        enemyNode.attachChild(seeker);
    }

    public void createWanderer() {
        Spatial wanderer = blasterMain.getSpatial("Wanderer");
        wanderer.setLocalTranslation(getSpawnPosition());
        wanderer.addControl(new WandererControl(settings.getWidth(), settings.getHeight()));
        wanderer.setUserData("active",false);
        enemyNode.attachChild(wanderer);
    }  
    
    public void createBlackHole() {
       Spatial blackHole = blasterMain.getSpatial("Black Hole");
       blackHole.setLocalTranslation(getSpawnPosition());
       blackHole.addControl(new BlackHoleControl(particleManager, grid));
       blackHole.setUserData("active",false);
       blackHoleNode.attachChild(blackHole);        
    }
    
    public void createParticle() {
       Spatial particle = blasterMain.getSpatial("Particle");
       particle.setLocalTranslation(getSpawnPosition());
       particle.addControl(new BlackHoleControl(particleManager, grid));
       particle.setUserData("affectedByGravity", true);
       blackHoleNode.attachChild(particle); 
    }
    
    
    public Vector3f getAimDirection() {
        Vector2f mouse = inputManager.getCursorPosition();
        Vector3f playerPos = player.getLocalTranslation();
        Vector3f dif = new Vector3f(mouse.x-playerPos.x,mouse.y-playerPos.y,0);
        return dif.normalizeLocal();
    }  
    
    public static float getAngleFromVector(Vector3f vec) {
        Vector2f vec2 = new Vector2f(vec.x,vec.y);
        return vec2.getAngle();
    }

    public static Vector3f getVectorFromAngle(float angle) {
        return new Vector3f(FastMath.cos(angle),FastMath.sin(angle),0);
    }
    
    public void spawnEnemies() {
        
        if (System.currentTimeMillis() - enemySpawnCooldown >= 17) {
            enemySpawnCooldown = System.currentTimeMillis();

            if (enemyNode.getQuantity() < 50) {
                if (new Random().nextInt((int) enemySpawnChance) == 0) {
                    createSeeker();
                }
                if (new Random().nextInt((int) enemySpawnChance) == 0) {
                    createWanderer();
                }
            }
            //increase Spawn Time
            if (enemySpawnChance >= 1.1f) {
                enemySpawnChance -= 0.005f;
            }
        }
    }
    
    
  
    public Vector3f getSpawnPosition() {
        Vector3f pos;
        do {
            pos = new Vector3f(new Random().nextInt(settings.getWidth()), 
                    new Random().nextInt(settings.getHeight()),0);
        } while (pos.distanceSquared(player.getLocalTranslation()) < 8000);
        
        return pos;
    }    

    public void handleCollisions() {
        
       // should the player die?
       for (int i=0; i<enemyNode.getQuantity(); i++) {
           if ((Boolean) enemyNode.getChild(i).getUserData("active")) {
               if (checkCollision(player,enemyNode.getChild(i))) {
                   killPlayer();
               }
           }           
       }
       
       //should an enemy die?
        int i=0;
        while (i < enemyNode.getQuantity()) {
                   
            int j=0;
            while (j < bulletNode.getQuantity()) {
                if (checkCollision(enemyNode.getChild(i),bulletNode.getChild(j))) {
                    
                    // add points depending on the type of enemy
                    if (enemyNode.getChild(i).getName().equals("Seeker")) {
                        hud.addPoints(2);
                    } else if (enemyNode.getChild(i).getName().equals("Wanderer")) {
                        hud.addPoints(1);
                    }
                    
                    particleManager.enemyExplosion(enemyNode.getChild(i).getLocalTranslation());
                    enemyNode.detachChildAt(i);
                    bulletNode.detachChildAt(j);
                    break;
                }
                j++;
            }
            i++;
        }
        
        //is something colliding with a black hole?
       for (i=0; i<blackHoleNode.getQuantity(); i++) {
           Spatial blackHole = blackHoleNode.getChild(i);
           
           if ((Boolean) blackHole.getUserData("active")) {
               //player
               if (checkCollision(player,blackHole)) {
                   killPlayer();
               }

               //enemies
               int j=0;
               while (j < enemyNode.getQuantity()) {
                   if (checkCollision(enemyNode.getChild(j),blackHole)) {
                       particleManager.enemyExplosion(enemyNode.getChild(j).getLocalTranslation());
                       enemyNode.detachChildAt(j);
                   }
                   j++;
               }

               //bullets
               j=0;
               while (j < bulletNode.getQuantity()) {
                   if (checkCollision(bulletNode.getChild(j),blackHole)) {
                       bulletNode.detachChildAt(j);
                       blackHole.getControl(BlackHoleControl.class).wasShot();
                       particleManager.blackHoleExplosion(blackHole.getLocalTranslation());
                       if (blackHole.getControl(BlackHoleControl.class).isDead()) {
                           blackHoleNode.detachChild(blackHole);
                           sound.explosion();
                       }
                   }
                   j++;
               }
           }
       }         
   }   

    public void handleGravity(float tpf) {
        
        for (int i=0; i<blackHoleNode.getQuantity(); i++) {
            if (!(Boolean)blackHoleNode.getChild(i).getUserData("active")) {continue;}
            int radius = 250;

            //check Player
            if (isNearby(player,blackHoleNode.getChild(i),radius)) {
                applyGravity(blackHoleNode.getChild(i), player, tpf);
            }
            //check Bullets
            for (int j=0; j<bulletNode.getQuantity(); j++) {
                if (isNearby(bulletNode.getChild(j),blackHoleNode.getChild(i),radius)) {
                    applyGravity(blackHoleNode.getChild(i), bulletNode.getChild(j), tpf);
                }
            }
            //check Enemies
            for (int j=0; j<enemyNode.getQuantity(); j++) {
                if (!(Boolean)enemyNode.getChild(j).getUserData("active")) {continue;}
                if (isNearby(enemyNode.getChild(j),blackHoleNode.getChild(i),radius)) {
                    applyGravity(blackHoleNode.getChild(i), enemyNode.getChild(j), tpf);
                }
            }
            
            // check Particles
            for (int j=0; j< particleNode.getQuantity(); j++) {
                if (particleNode.getChild(j).getUserData("affectedByGravity")) {
                    applyGravity(blackHoleNode.getChild(i), particleNode.getChild(j), tpf);
                }
            }   
        }
    }    
    
    public boolean checkCollision(Spatial a, Spatial b) {
        
        float distance = a.getLocalTranslation().distance(b.getLocalTranslation());
        float maxDistance =  (Float)a.getUserData("radius") + (Float)b.getUserData("radius");
        return distance <= maxDistance;
    }

    public void killPlayer() {
        
        if (!hud.removeLife()) {
            hud.endGame();
            blasterMain.setGameOver(true);
        }
        
        particleManager.playerExplosion(player.getLocalTranslation());
        player.removeFromParent();
        player.getControl(PlayerControl.class).reset();
        player.setUserData("alive", false);
        player.setUserData("dieTime", System.currentTimeMillis());
        enemyNode.detachAllChildren();
        blackHoleNode.detachAllChildren();
    }
    
    public void spawnBlackHole() {
        if (blackHoleNode.getQuantity() < 2) {
            if (System.currentTimeMillis() - blackHoleSpawnCooldown > 10f) {
            blackHoleSpawnCooldown = System.currentTimeMillis();
            if (new Random().nextInt(1000) == 0) {
                createBlackHole();
            }
        }
       }
    }
    
   private boolean isNearby(Spatial a, Spatial b, float distance) {
        Vector3f pos1 = a.getLocalTranslation();
        Vector3f pos2 = b.getLocalTranslation();
        return pos1.distanceSquared(pos2) <= distance * distance;
    } 
   
   private void applyGravity(Spatial blackHole, Spatial target, float tpf) {
       
        Vector3f difference = blackHole.getLocalTranslation().subtract(target.getLocalTranslation());

        Vector3f gravity = difference.normalize().multLocal(tpf);
        float distance = difference.length();

        if (target.getName().equals("Player")) {
            gravity.multLocal(250f/distance);
            target.getControl(PlayerControl.class).applyGravity(gravity.mult(80f));
        } else if (target.getName().equals("Bullet")) {
            gravity.multLocal(250f/distance);
            target.getControl(BulletControl.class).applyGravity(gravity.mult(-0.8f));
        } else if (target.getName().equals("Seeker")) {
            target.getControl(SeekerControl.class).applyGravity(gravity.mult(150000));
        } else if (target.getName().equals("Wanderer")) {
            target.getControl(WandererControl.class).applyGravity(gravity.mult(150000));
        } else if (target.getName().equals("Laser") || target.getName().equals("Glow")) {
            target.getControl(ParticleControl.class).applyGravity(gravity.mult(15000), distance);
        }
    }
}
