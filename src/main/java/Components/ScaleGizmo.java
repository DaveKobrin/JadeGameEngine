package Components;

import Editor.PropertiesWindow;
import Jade.MouseListener;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo{

    public ScaleGizmo(final Sprite scaleSprite, final PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void update(float dt){

        if (activeGO != null) {
            if (xAxisActive) {
                activeGO.getTransform().setScale(new Vector2f(activeGO.getTransform().getScale().x + MouseListener.getWorldVPDX(), activeGO.getTransform().getScale().y));
            }else if (yAxisActive) {
                activeGO.getTransform().setScale(new Vector2f(activeGO.getTransform().getScale().x, activeGO.getTransform().getScale().y + MouseListener.getWorldVPDY()));
            }
        }

        super.update(dt);
    }
}
