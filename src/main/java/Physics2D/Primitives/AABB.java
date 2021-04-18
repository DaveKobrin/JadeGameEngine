package Physics2D.Primitives;

import Physics2D.RigidBody.RigidBody2D;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

/**
 * AABB - Axis Aligned Bounding Box
 */
public class AABB extends Collider2D{
    @Setter
    @Getter private RigidBody2D rigidBody = new RigidBody2D();
    @Getter private Vector2f size = new Vector2f();
    @Getter private Vector2f halfSize = new Vector2f();


    public AABB() {
    }

    public AABB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(this.size).div(2.0f);
        this.rigidBody.setPosition(new Vector2f(min).add(this.halfSize));

    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidBody.getPosition()).add(this.halfSize);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidBody.getPosition()).sub(this.halfSize);
    }

    public void setSize(final Vector2f size) {
        this.size.set(size);
        this.halfSize.set(new Vector2f(size).div(2.0f));
    }

    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y)
        };

        return vertices;
    }

}
