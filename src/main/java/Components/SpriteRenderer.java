package Components;

import Jade.Component;
import Jade.Transform;
import Renderer.Texture;
import Utility.Color;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * SpriteRenderer - a component of a GameObject that tracks changes to the position,
 *                  scale, color and/or Sprite associated with the GameObject
 */
public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1.0f,1.0f,1.0f,1.0f);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform = new Transform();    // track changes in position and scale
    private transient boolean changed = true;    // has the sprite changed since last frame


//    public SpriteRenderer(Vector4f color) {
//        this.color = color;
//        this.sprite = new Sprite();
//        this.changed = true;
//    }
//
//    public SpriteRenderer(Sprite sprite) {
//        this.color = new Vector4f(1.0f,1.0f,1.0f,1.0f);
//        this.sprite = sprite;
//        this.changed = true;
//    }

    @Override
    public void start() {
        Transform.copyValues(this.gameObject.getTransform(), this.lastTransform);
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.getTransform())) {
            Transform.copyValues(this.gameObject.getTransform(), this.lastTransform);
            this.changed = true;
        }
    }

    /**
     * imGui - override of Component.imGui() - handle SpriteRender specific GUI tasks
     */
    @Override
    public void imGui() {
        float[] colorArr = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Choose a color", colorArr)) {
            this.color.set(colorArr);
            this.changed = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return this.sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return this.sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.changed = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.changed = true;
        }
    }

    public void setColor(Color color) {
        setColor(new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }

    /**
     * isChanged() - true if sprite has changed in any way this frame.
     *               used to determine need to re-upload vertex data to the GPU
     * @return - changed
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * resetChanged() - use to set changed to false when vertex data is sent to GPU
     */
    public void resetChanged() {
        this.changed = false;
    }

    public void setTexture(Texture tex) {
        this.sprite.setTexture(tex);
        this.changed = true;
    }
}
