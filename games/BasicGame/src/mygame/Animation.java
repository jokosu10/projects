/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author ARIS CRUZ
 */
public class Animation extends SimpleApplication implements AnimEventListener {

    private AnimChannel animationChannel, animationChannel2;
    private AnimControl animationControl;
    protected Node player;
    
    public static void main(String[] args) {
        
        Animation animation = new Animation();
        animation.start();
        
    }
    
    @Override
    public void simpleInitApp() {
        
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        player.setLocalScale(0.5f);
        rootNode.attachChild(player);
        animationControl = player.getControl(AnimControl.class);
        animationControl.addListener(this);
        animationChannel = animationControl.createChannel();
        animationChannel.setAnim("stand");
        
        animationChannel2 = animationControl.createChannel();
        
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
       if (animName.equals("Walk")) {
        channel.setAnim("stand", 0.50f);
        channel.setLoopMode(LoopMode.DontLoop);
        channel.setSpeed(1f);
       } else if (animName.equals("pull")) {
         channel.setAnim("Walk", 0.50f);
         channel.setLoopMode(LoopMode.DontLoop);
         channel.setSpeed(1f);
       }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        
    }

    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("pull", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Walk");
        inputManager.addListener(analogListener, "pull");
    }
 
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("Walk") && !keyPressed) {
            if (!animationChannel.getAnimationName().equals("Walk")) {
              animationChannel.setAnim("Walk", 0.50f);
              animationChannel.setLoopMode(LoopMode.Loop);
            }
          }
        }
      };

      private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("pull")) {
                animationChannel2.setAnim("pull", 1f);
                animationChannel2.setLoopMode(LoopMode.Loop);
            }
        }
      };
    
    
}
