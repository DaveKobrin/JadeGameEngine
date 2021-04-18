package Physics2D.Forces;

import Physics2D.RigidBody.RigidBody2D;
import org.joml.Vector2f;

public class Gravity2D implements ForceGenerator{

    private Vector2f gravity = new Vector2f();

    public Gravity2D(Vector2f force) {
        this.gravity.set(force);
    }
    @Override
    public void updateForce(RigidBody2D rigidBody2D, float dt) {
        rigidBody2D.addForce(new Vector2f(gravity).mul(rigidBody2D.getMass()));
    }
}
