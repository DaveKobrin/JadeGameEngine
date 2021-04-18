package Physics2D.RigidBody;

import Jade.Component;
import Jade.Transform;
import Physics2D.Primitives.Collider2D;
import Utility.JMath;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;


public class RigidBody2D extends Component {
    @Setter     @Getter
    private Vector2f position = new Vector2f();

    @Setter     @Getter
    private float rotation = 0.0f;

    @Setter     @Getter
    private Vector2f linearVelocity = new Vector2f();

    @Setter     @Getter
    private float elasticity = 1f;              // coefficient of restitution

    @Setter     @Getter
    private float angularVelocity = 0.0f;

    @Setter     @Getter
    private float linearDamping = 0.0f;

    @Setter     @Getter
    private float angularDamping = 0.0f;

    @Setter     @Getter
    private Vector2f forceAccumulator = new Vector2f();     //holds the total sum of all linear forces

    @Setter     @Getter
    private Collider2D collider = null;

    @Getter
    private float mass = 0f;

    @Getter
    private float inverseMass = 0f;

    private Transform rawTransform = null;


    private boolean fixedRotation = false;

    public void setTransform(final Vector2f position, final float rotation) {
        this.position.set(position);
    }

    public void setTransform(final Vector2f position) {
        this.position.set(position);
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0f)
            inverseMass = 1f / this.mass;
    }

    public void physicsUpdate(float dt) {
        if (JMath.compare(this.mass, 0f))       //if mass is 0f treat as immovable object
            return;

        //calculate linear velocity
        Vector2f acceleration = new Vector2f(forceAccumulator).mul(inverseMass);
        linearVelocity.add(acceleration.mul(dt));

        //calc new position
        position.add(new Vector2f(linearVelocity).mul(dt));

        syncCollisionTransforms();
        clearAccumulators();
    }

    public void clearAccumulators() {
        this.forceAccumulator.zero();
    }

    public void syncCollisionTransforms() {
        if (rawTransform != null) {
            rawTransform.setPosition(this.position);
        }
    }

    public void addForce(Vector2f force) {
        this.forceAccumulator.add(force);
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position = this.rawTransform.getPosition();
    }

    public boolean hasInfiniteMass() { return this.mass == 0f;}
}
