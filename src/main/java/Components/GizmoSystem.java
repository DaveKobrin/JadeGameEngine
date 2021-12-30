package Components;

import Jade.Component;
import Jade.KeyListener;
import Jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {
    enum AvailableGizmos {
        TRANSLATE,
        SCALE;
    }

    private AvailableGizmos activeGizmo = AvailableGizmos.TRANSLATE;
    private Spritesheet gizmoSS;

    public GizmoSystem ( Spritesheet gizmoSS) {
        this.gizmoSS = gizmoSS;
    }

    @Override
    public void update(float dt) {
        switch (activeGizmo) {
            case TRANSLATE:
                gameObject.getComponent(TranslateGizmo.class).setUsingGizmo(true);
                gameObject.getComponent(ScaleGizmo.class).setUsingGizmo(false);
                break;
            case SCALE:
                gameObject.getComponent(TranslateGizmo.class).setUsingGizmo(false);
                gameObject.getComponent(ScaleGizmo.class).setUsingGizmo(true);
                break;
            default:
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E)){
            activeGizmo = AvailableGizmos.TRANSLATE;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            activeGizmo = AvailableGizmos.SCALE;
        }

    }
}
