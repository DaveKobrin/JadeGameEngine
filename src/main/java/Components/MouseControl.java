package Components;

import Editor.GameViewWindow;
import Jade.*;
import Utility.AssetPool;
import Utility.Settings;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {

    GameObject objectHeld = null;

    public void attach(GameObject obj) {
        objectHeld = obj;
        Window.getScene().addGameObject2Scene(objectHeld);

    }

    public void detach(){
        System.out.println("dropped at : (" + MouseListener.getOrthoX() + ", " + MouseListener.getOrthoY() + ")");
        System.out.println("object transform : " + objectHeld.getTransform().getPosition());
        objectHeld = null;
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            System.out.println("space bar pressed");
            GameObject go = new GameObject("test", new Transform(new Vector2f(100,100), new Vector2f(200,200)), 0);
            Sprite spr = new Sprite();
            spr.setTexture(AssetPool.getTexture("assets/textures/decorationsAndBlocks.png"));
            SpriteRenderer goSprRend = new SpriteRenderer();
            goSprRend.setSprite(spr);
            go.addComponent(goSprRend);
            Window.getScene().addGameObject2Scene(go);
        }
        if (objectHeld != null) {
            Vector2f newPos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
//            Vector2f newPos = new Vector2f(MouseListener.getViewportOrthoX(), MouseListener.getViewportOrthoY());
            newPos.x = ((int)newPos.x / Settings.TILE_WIDTH) * Settings.TILE_WIDTH;
            newPos.y = ((int)newPos.y / Settings.TILE_HEIGHT) * Settings.TILE_HEIGHT;
//            System.out.println(newPos);
            objectHeld.getTransform().setPosition(newPos);

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
//                           &&  GameViewWindow.isInViewport(newPos.x, newPos.y)
            ) {
                detach();
            }
        }
    }
}
