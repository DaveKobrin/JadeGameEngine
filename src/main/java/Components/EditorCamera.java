package Components;

import Jade.Camera;
import Jade.Component;
import Jade.KeyListener;
import Jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {

    private final float DRAG_SENSITIVITY = 30f;
    private final float DRAG_DEBOUNCE = 3f / 60f; //debounce set to 3 frames running at 60fps
    private final float ZOOM_SENSITIVITY = 0.02f;

    private float dragDebounce;
    private boolean resetting = false;
    private float lerpTime = 0f;

    private final Camera levelEditorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
        this.dragDebounce = DRAG_DEBOUNCE;
    }

    @Override
    public void update(float dt){
        //dragging controls
        Vector2f mousePos = new Vector2f(MouseListener.getViewportOrthoX(), MouseListener.getViewportOrthoY());
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0f) {
            this.clickOrigin = new Vector2f(mousePos);
            dragDebounce -= dt;
            return;
        }
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            this.levelEditorCamera.setPosition(this.levelEditorCamera.getPosition().sub(delta.mul(dt).mul(DRAG_SENSITIVITY)));
            this.clickOrigin.lerp(mousePos, dt);
        }
        if (dragDebounce <= 0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = DRAG_DEBOUNCE;
        }

        //zoom controls
        if (MouseListener.getScrollY() != 0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY()) * ZOOM_SENSITIVITY, 1f / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        //reset zoom and camera position
        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            resetting = true;
        }
        if (resetting) {
            levelEditorCamera.setPosition(levelEditorCamera.getPosition().lerp(new Vector2f(), lerpTime));
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() + ((1f - levelEditorCamera.getZoom()) * lerpTime));
            lerpTime += 0.1f * dt;

            if (Math.abs(levelEditorCamera.getPosition().x) <= 5f && Math.abs(levelEditorCamera.getPosition().y) <= 5f) {
                levelEditorCamera.setPosition(new Vector2f());
                levelEditorCamera.setZoom(1f);
                resetting = false;
                lerpTime = 0f;
            }
        }
    }
}
