package Components;

import Editor.PropertiesWindow;
import Jade.*;
import Renderer.DebugDrawBatch;
import Utility.Color;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {

    private final Color xAxisColor = Color.COLORS.RED.getAsColor();
    private final Color xAxisHover = Color.COLORS.MAGENTA.getAsColor();
    private final Color yAxisColor = Color.COLORS.GREEN.getAsColor();
    private final Color yAxisHover = Color.COLORS.CYAN.getAsColor();

    private final int GIZMO_WIDTH = 24;
    private final int GIZMO_HEIGHT = 48;

    private Vector2f xAxisOffset = new Vector2f(64f,0f);
    private Vector2f yAxisOffset = new Vector2f(24f,64f);
    protected GameObject activeGO = null;
    private GameObject xAxisObj;
    private GameObject yAxisObj;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private PropertiesWindow propertiesWindow;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    @Getter @Setter
    protected boolean gizmoActive;
    @Getter
    protected boolean usingGizmo;

    public Gizmo(final Sprite arrowSprite, final PropertiesWindow propertiesWindow) {
        this.propertiesWindow = propertiesWindow;
        xAxisObj = PreFabs.generateGizmo(arrowSprite, GIZMO_WIDTH, GIZMO_HEIGHT);
        yAxisObj = PreFabs.generateGizmo(arrowSprite, GIZMO_WIDTH, GIZMO_HEIGHT);
        xAxisObj.setNoSerialize();
        yAxisObj.setNoSerialize();
        xAxisObj.addComponent(new NotPickable());
        yAxisObj.addComponent(new NotPickable());
        xAxisSprite = xAxisObj.getComponent(SpriteRenderer.class);
        yAxisSprite = yAxisObj.getComponent(SpriteRenderer.class);

        Window.getScene().addGameObject2Scene(xAxisObj);
        Window.getScene().addGameObject2Scene(yAxisObj);
    }

    @Override
    public void start(){
        xAxisObj.getTransform().setRotation(90f);
        yAxisObj.getTransform().setRotation(180f);
    }

    @Override
    public void update(float dt){
        if (!usingGizmo)
            return;

        activeGO = propertiesWindow.getActiveGameObject();
        if (activeGO != null) {
            setActive();
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXAxisHoverState();
        boolean yAxisHot = checkYAxisHoverState();

        if ((xAxisHot || xAxisActive) && !yAxisActive && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && !xAxisActive && MouseListener.isDragging() && MouseListener.mouseButtonDown( GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }
    }

    private boolean checkXAxisHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getViewportOrthoX(), MouseListener.getViewportOrthoY());

//        DebugDrawBatch.addBoxCorners(new Vector2f(xAxisObj.getTransform().getPosition()),new Vector2f(xAxisObj.getTransform().getPosition().x - GIZMO_HEIGHT, xAxisObj.getTransform().getPosition().y + GIZMO_WIDTH), Color.COLORS.ORANGE.getAsColor());

        if (    mousePos.x <= xAxisObj.getTransform().getPosition().x &&
                mousePos.x >= xAxisObj.getTransform().getPosition().x - GIZMO_HEIGHT &&
                mousePos.y >= xAxisObj.getTransform().getPosition().y &&
                mousePos.y <= xAxisObj.getTransform().getPosition().y + GIZMO_WIDTH ) {
            xAxisSprite.setColor(xAxisHover);
            return true;
        }
        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYAxisHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getViewportOrthoX(), MouseListener.getViewportOrthoY());

//        DebugDrawBatch.addBoxCorners(new Vector2f(yAxisObj.getTransform().getPosition()),new Vector2f(yAxisObj.getTransform().getPosition().x - GIZMO_WIDTH, yAxisObj.getTransform().getPosition().y - GIZMO_HEIGHT), Color.COLORS.ORANGE.getAsColor());

        if (    mousePos.x <= yAxisObj.getTransform().getPosition().x &&
                mousePos.x >= yAxisObj.getTransform().getPosition().x - GIZMO_WIDTH &&
                mousePos.y <= yAxisObj.getTransform().getPosition().y &&
                mousePos.y >= yAxisObj.getTransform().getPosition().y - GIZMO_HEIGHT ) {
            yAxisSprite.setColor(yAxisHover);
            return true;
        }
        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    private void setActive(){
        // move gizmo sprites to position of active game object
        xAxisObj.getTransform().setPosition(new Vector2f(activeGO.getTransform().getPosition()).add(xAxisOffset));
        yAxisObj.getTransform().setPosition(new Vector2f(activeGO.getTransform().getPosition()).add(yAxisOffset));
        //make visible
        xAxisSprite.setColor(xAxisColor);
        yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive(){
        //make invisible
        xAxisSprite.setColor(Color.COLORS.TRANSPARENT.getAsColor());
        yAxisSprite.setColor(Color.COLORS.TRANSPARENT.getAsColor());
    }

    public void setUsingGizmo(boolean usingGizmo) {
        this.usingGizmo = usingGizmo;
        if (!usingGizmo)
            setInactive();
    }
}
