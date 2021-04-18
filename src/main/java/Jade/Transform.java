package Jade;

import org.joml.Vector2f;

/**
 * Transform - contains the position and scale information for GameObjects
 */
public class Transform {
    private Vector2f position;
    private Vector2f scale;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public Transform(Transform transform) { init(transform.getPosition(), transform.getScale()); }

    private void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }
    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    public Transform copy(Transform t) {
        Transform result = new Transform();
        result.setPosition(t.getPosition());
        result.setScale(t.getScale());
        return result;
    }

    public static void copyValues(Transform src, Transform dest) {
        dest.setPosition(src.getPosition());
        dest.setScale(src.getScale());
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null || (!(obj instanceof Transform))) {
            return false;
        }
        Transform t = (Transform) obj;
        return (t.position.equals(this.position) && t.scale.equals(this.scale));
    }
}
