package Jade;

import Editor.JImGui;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

/**
 * Transform - contains the position and scale information for GameObjects
 */
public class Transform extends Component{
    @Getter @Setter
    private Vector2f position;
    @Getter @Setter
    private Vector2f scale;
    @Getter @Setter
    private float rotation;
    @Getter @Setter
    private int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f(), 0f, 0);
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f(), 0f, 0);
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale , 0f, 0);
    }

    public Transform(Vector2f position, float rotation) { init(position, new Vector2f(), rotation, 0); }

    public Transform(Vector2f position, int zIndex) { init(position, new Vector2f(), 0, zIndex);}

    public Transform(Transform transform) { init(transform.getPosition(), transform.getScale(), transform.getRotation(), transform.getZIndex()); }

    private void init(Vector2f position, Vector2f scale, float rotation, int zIndex) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.zIndex = zIndex;
    }

    public Transform copy() {
        Transform result = new Transform();
        result.setPosition(this.getPosition());
        result.setScale(this.getScale());
        result.setRotation(this.getRotation());
        result.setZIndex(this.getZIndex());
        return result;
    }

    public static void copyValues(Transform src, Transform dest) {
        dest.setPosition(src.getPosition());
        dest.setScale(src.getScale());
        dest.setRotation(src.getRotation());
        dest.setZIndex(src.getZIndex());
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null || (!(obj instanceof Transform))) {
            return false;
        }
        Transform t = (Transform) obj;
        return (t.position.equals(this.position) && t.scale.equals(this.scale) && t.rotation == this.rotation && t.zIndex == this.zIndex);
    }

    @Override
    public void imGui() {
        JImGui.vec2fControl("Position", position);
        JImGui.vec2fControl("Scale", scale, 32);
    }
}
