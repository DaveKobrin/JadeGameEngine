package Components;

import Editor.PropertiesWindow;
import Jade.MouseListener;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo{

    public TranslateGizmo(final Sprite arrowSprite, final PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void update(float dt){

        if (activeGO != null) {
            if (xAxisActive) {
                activeGO.getTransform().setPosition(new Vector2f(activeGO.getTransform().getPosition().x + MouseListener.getWorldVPDX(), activeGO.getTransform().getPosition().y));
            }else if (yAxisActive) {
                activeGO.getTransform().setPosition(new Vector2f(activeGO.getTransform().getPosition().x, activeGO.getTransform().getPosition().y + MouseListener.getWorldVPDY()));
            }
        }

        super.update(dt);
    }
}
