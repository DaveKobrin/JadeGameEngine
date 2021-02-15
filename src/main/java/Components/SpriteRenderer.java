package Components;

import Jade.Component;
import Jade.Transform;
import Renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color;
    private Sprite sprite;

    private Transform lastTransform;    // track changes in position and scale
    private boolean changed = false;    // has the sprite changed since last frame

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
        this.changed = true;
    }

    public SpriteRenderer(Sprite sprite) {
        this.color = new Vector4f(1.0f,1.0f,1.0f,1.0f);
        this.sprite = sprite;
        this.changed = true;
    }

    @Override
    public void start() {
        this.lastTransform = this.gameObject.getTransform();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.getTransform())) {
            this.lastTransform = this.gameObject.getTransform();
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

    public boolean isChanged() {
        return this.changed;
    }

    public void resetChanged() {
        this.changed = false;
    }
}
