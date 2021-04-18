package Physics2D.Forces;

import Physics2D.RigidBody.RigidBody2D;

public interface ForceGenerator {
    void updateForce(RigidBody2D rigidBody2D, float dt);
}
