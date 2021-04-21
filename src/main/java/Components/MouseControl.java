package Components;

import Editor.GameViewWindow;
import Jade.*;
import Utility.AssetPool;
import Utility.Settings;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {

    private final int debugDelayMax = 300;
    private int debugDelayCount = 0;

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
        if (objectHeld != null) {
//            Vector2f newPos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            if (++debugDelayCount >= debugDelayMax) {       //allow for breakpoint debugDelayMax frames after picked block
                debugDelayCount = 0;
            }
            float x = MouseListener.getViewportOrthoX();
            float y = MouseListener.getViewportOrthoY();
            Vector2f newPos = new Vector2f(x, y);
            newPos.x = ((int)newPos.x / Settings.TILE_WIDTH) * Settings.TILE_WIDTH;
            newPos.y = ((int)newPos.y / Settings.TILE_HEIGHT) * Settings.TILE_HEIGHT;
            System.out.println(newPos);
            objectHeld.getTransform().setPosition(newPos);

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                           &&  GameViewWindow.isInViewport(newPos.x, newPos.y)
            ) {
                detach();
            }
        }
    }
}
