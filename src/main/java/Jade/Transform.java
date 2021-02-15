package Jade;

import org.joml.Vector2f;

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

    public Transform(Transform transform) {
        init(transform.getPosition(), transform.getScale());
    }
    private void init(Vector2f position, Vector2f scale) {
        this.position = new Vector2f(position);
        this.scale = new Vector2f(scale);
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

    @Override
    public boolean equals(Object obj){
        if (obj == null || (!(obj instanceof Transform))) {
            return false;
        }
        Transform t = (Transform) obj;
        return (t.position.equals(this.position) && t.scale.equals(this.scale));
    }
}
