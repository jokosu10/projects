package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import java.awt.Rectangle;
import mygame.controls.BulletControl;
import mygame.controls.PlayerControl;
import mygame.utils.CustomFunctions;
import mygame.utils.Grid;
import mygame.utils.Hud;
import mygame.utils.ParticleManager;
import mygame.utils.Sound;

/**
 * 
 * @author ARIS CRUZ
 */
public class BlasterMain extends SimpleApplication implements ActionListener, AnalogListener {

    public static void main(String[] args) {
        BlasterMain app = new BlasterMain();
        app.start();
    }

    private Spatial player;
    private Node bulletNode, enemyNode, blackHoleNode, particleNode;
    private Long bulletCooldown = 0L;
    private long enemySpawnCooldown, blackHoleSpawnCoolDown;
    private float enemySpawnChance = 80;
    private Sound sound;
    private Hud hud;
    private ParticleManager particleManager;
    private CustomFunctions customFunctions;
    private boolean gameOver;
    private Grid grid;
    
    @Override
    public void simpleInitApp() {
   
        //setup camera for 2D games
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0,0,0.5f));
        getFlyByCamera().setEnabled(false);
 
        //turn off stats view (you can leave it on, if you want)
        setDisplayStatView(false);
        setDisplayFps(false);
        
                //setup the bulletNode
        bulletNode = new Node("bullets");
        guiNode.attachChild(bulletNode);        

        // set up the enemyNode
        enemyNode = new Node("enemies");
        guiNode.attachChild(enemyNode);
        
        blackHoleNode = new Node("blackhole");
        guiNode.attachChild(blackHoleNode);
        
        particleNode = new Node("particles");
        guiNode.attachChild(particleNode);
        
        particleManager = new ParticleManager(guiNode, getSpatial("Laser"), getSpatial("Glow"), settings);
        
        //setup the player
        player = getSpatial("Player");
        player.setUserData("alive",true);
        player.move(settings.getWidth()/2, settings.getHeight()/2, 0);
        player.addControl(new PlayerControl(settings.getWidth(), settings.getHeight(), particleManager));
        guiNode.attachChild(player);   
        
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("return", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "left");
        inputManager.addListener(this, "right");
        inputManager.addListener(this, "up");
        inputManager.addListener(this, "down");
        inputManager.addListener(this, "return");
        
        inputManager.addMapping("mousePick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "mousePick");
        
        inputManager.setMouseCursor((JmeCursor) assetManager.loadAsset("Textures/Pointer.ico"));
        
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom=new BloomFilter();
        bloom.setBloomIntensity(2f);
        bloom.setExposurePower(2);
        bloom.setExposureCutOff(0f);
        bloom.setBlurScale(1.5f);
        fpp.addFilter(bloom);
        guiViewPort.addProcessor(fpp);
        guiViewPort.setClearColor(true);       
        
        sound = new Sound(assetManager);
        sound.startMusic();
        sound.spawn();
       
        hud = new Hud(assetManager, guiNode, settings.getWidth(), settings.getHeight());
        hud.reset();        
        
        Rectangle size = new Rectangle(0, 0, settings.getWidth(), settings.getHeight());
        Vector2f spacing = new Vector2f(25,25);
        grid = new Grid(size, spacing, guiNode, assetManager);
        
        customFunctions = new CustomFunctions(inputManager, player, bulletNode,
        enemyNode, blackHoleNode, particleNode, enemySpawnCooldown, blackHoleSpawnCoolDown, 
                enemySpawnChance, settings, sound, hud, particleManager, grid, this); 
           
    }
 
    @Override
    public void simpleUpdate(float tpf) {

        if ((Boolean) player.getUserData("alive")) {
           customFunctions.spawnEnemies();
           customFunctions.spawnBlackHole();
           customFunctions.handleCollisions();
           customFunctions.handleGravity(tpf);
           
        } else if (System.currentTimeMillis() - (Long) player.getUserData("dieTime") > 4000f && !isGameOver()) {
            // spawn player
            player.setLocalTranslation(500,500,0);
            guiNode.attachChild(player);
            player.setUserData("alive",true);
            sound.spawn();
            grid.applyDirectedForce(new Vector3f(0,0,5000), player.getLocalTranslation(), 100);
        }
        grid.update(tpf);
        hud.update();
    }
 
    @Override
    public void simpleRender(RenderManager rm) {}
    
 
    /* on keyboard press */
    public void onAction(String name, boolean isPressed, float tpf) {
      
        if ((Boolean) player.getUserData("alive")) {    
            if (name.equals("up")) {
               player.getControl(PlayerControl.class).up = isPressed;
            } else if (name.equals("down")) {
                player.getControl(PlayerControl.class).down = isPressed;
            } else if (name.equals("left")) {
                player.getControl(PlayerControl.class).left = isPressed;
            } else if (name.equals("right")) {
                player.getControl(PlayerControl.class).right = isPressed;
            }
        } 
    }
    
    /* on mouse clicks */
    public void onAnalog(String name, float value, float tpf) {
        
        if ((Boolean) player.getUserData("alive")) {
            if (name.equals("mousePick")) {
                //shoot Bullet
                if (System.currentTimeMillis() - bulletCooldown > 83f) {
                    bulletCooldown = System.currentTimeMillis();
 
                    Vector3f aim = customFunctions.getAimDirection();
                    Vector3f offset = new Vector3f(aim.y/3,-aim.x/3,0);
 
                    //init bullet 1
                    Spatial bullet = getSpatial("Bullet");
                    Vector3f finalOffset = aim.add(offset).mult(30);
                    Vector3f trans = player.getLocalTranslation().add(finalOffset);
                    bullet.setLocalTranslation(trans);
                    bullet.addControl(new BulletControl(aim, settings.getWidth(), 
                                settings.getHeight(), particleManager, grid));
                    bulletNode.attachChild(bullet);
 
                    //init bullet 2
                    Spatial bullet2 = getSpatial("Bullet");
                    finalOffset = aim.add(offset.negate()).mult(30);
                    trans = player.getLocalTranslation().add(finalOffset);
                    bullet2.setLocalTranslation(trans);
                    bullet2.addControl(new BulletControl(aim, settings.getWidth(), 
                                settings.getHeight(), particleManager, grid));
                    bulletNode.attachChild(bullet2);
                                        
                    sound.shoot();
                }
            }
        }
    }

   /* Code blocks for creation of characters */ 
   public Spatial getSpatial(String name) {
        
        Node node = new Node(name);
        //load picture
        Picture pic = new Picture(name);
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/"+name+".png");
        pic.setTexture(assetManager,tex,true);
 
        //adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(-width/2f,-height/2f,0);
 
        //add a material to the picture
        Material picMat = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        picMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        node.setMaterial(picMat);
 
        //set the radius of the spatial
        //(using width only as a simple approximation)
        node.setUserData("radius", width/2);
 
        //attach the picture to the node and return it
        node.attachChild(pic);
        return node;
    }     
     
  /* END */ 
   
    public AppSettings getSettings() {
        return settings;
    }

    /**
     * @return the gameOver
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @param gameOver the gameOver to set
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
