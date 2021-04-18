package Physics2D.Primitives;

import Physics2D.RigidBody.RigidBody2D;
import Utility.JMath;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

public class Box2D extends Collider2D {
    @Setter
    @Getter private RigidBody2D rigidBody = new RigidBody2D();
    @Getter private Vector2f size = new Vector2f();
    @Getter private Vector2f halfSize = new Vector2f();


    public Box2D() {
    }

    public Box2D(Vector2f min, Vector2f max, float rotation) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(this.size).div(2.0f);
        this.rigidBody.setPosition(new Vector2f(min).add(this.halfSize));
        this.rigidBody.setRotation(rotation);
    }

    public Vector2f getLocalMax() {
        return new Vector2f(this.rigidBody.getPosition()).add(this.halfSize);
    }

    public Vector2f getLocalMin() {
        return new Vector2f(this.rigidBody.getPosition()).sub(this.halfSize);
    }

    public void setSize(final Vector2f size) {
        this.size.set(size);
        this.halfSize.set(new Vector2f(size).div(2.0f));
    }

    public Vector2f[] getVertices() {
        Vector2f min = getLocalMin();
        Vector2f max = getLocalMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),

                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y)
        };

        if (!JMath.compare(this.rigidBody.getRotation(), 0.0f)) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, this.rigidBody.getRotation(), this.rigidBody.getPosition());
            }
        }
        return vertices;
    }
}
