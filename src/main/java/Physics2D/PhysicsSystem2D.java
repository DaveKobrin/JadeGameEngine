package Physics2D;

import Physics2D.Forces.ForceRegistry;
import Physics2D.Forces.Gravity2D;
import Physics2D.Primitives.Collider2D;
import Physics2D.RigidBody.CollisionManifold;
import Physics2D.RigidBody.Collisions2D;
import Physics2D.RigidBody.RigidBody2D;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem2D {
    private ForceRegistry forceRegistry = new ForceRegistry();
    private Gravity2D gravity2D = null;

    private List<RigidBody2D> rigidBodies = new ArrayList<>();

    private List<RigidBody2D> bodies1 = new ArrayList<>();        // three lists to contain linear collision info
    private List<RigidBody2D> bodies2 = new ArrayList<>();        // body1[i] collides with body2[i] with collision
    private List<CollisionManifold> collisions = new ArrayList<>(); // data in collisions[i]

    private float fixedUpdate;
    private final int IMPULSE_ITERATIONS = 6;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity){
        fixedUpdate = fixedUpdateDt;
        this.gravity2D = new Gravity2D(gravity);
    }

    public void update(float dt){
        fixedUpdate();
    }

    private void fixedUpdate(){
        bodies1.clear();
        bodies2.clear();
        collisions.clear();

        //update forces
        forceRegistry.updateForces(fixedUpdate);

        //find any collisions
        //todo find a better way to do this
        int size = rigidBodies.size();
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j)
                    continue;
                CollisionManifold result = new CollisionManifold();
                RigidBody2D body1 = rigidBodies.get(i);
                RigidBody2D body2 = rigidBodies.get(j);
                Collider2D coll1 = body1.getCollider();
                Collider2D coll2 = body2.getCollider();

                if (coll1 != null && coll2 != null && !(body1.hasInfiniteMass() && body2.hasInfiniteMass())) {
                    result = Collisions2D.findCollisionFeatures(coll1, coll2);
                }

                if (result != null && result.isColliding()) {
                    bodies1.add(body1);
                    bodies2.add(body2);
                    collisions.add(result);
                }
            }
        }

        //solve collisions via iterative impulse resolution
        for (int k = 0; k < IMPULSE_ITERATIONS; k++) {      // iteration of impulse resolution
            for (int i = 0; i < collisions.size(); i++) {   // loop through each collision
                int jSize = collisions.get(i).getCollisionPoints().size();
                for (int j = 0; j < jSize; j++) {           // loop through each contact point in the collision
                    applyImpulse(bodies1.get(i), bodies2.get(i), collisions.get(i));
                }
            }

        }

        //update rigidBodies' velocities
        for (RigidBody2D body : rigidBodies) {
            body.physicsUpdate(fixedUpdate);
        }
    }

    private void applyImpulse(RigidBody2D body1, RigidBody2D body2, CollisionManifold manifold) {
        float invMass1 = body1.getMass();
        float invMass2 = body2.getMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0f)               //if both are immovable do nothing
            return;

        //relative velocity
        Vector2f relativeVelocity = new Vector2f(body2.getLinearVelocity()).sub(body1.getLinearVelocity());
        Vector2f relativeNormal = manifold.getCollNormal();
        if (relativeVelocity.dot(relativeNormal) > 0f)       // bodies already moving apart
            return;

        //coefficient of restitution
        float e = Math.min(body1.getElasticity(), body2.getElasticity());

        //total impulse
        float j = (-(1f + e) * relativeVelocity.dot(relativeNormal)) / invMassSum;
        if (manifold.getCollisionPoints().size() > 1 && j != 0f)
            j /= (float) manifold.getCollisionPoints().size();      //spread impulse evenly over all contact points

        Vector2f impulse = new Vector2f(relativeNormal).mul(j);
        //apply the impulse to both bodies
        body1.setLinearVelocity(new Vector2f(body1.getLinearVelocity()).sub(new Vector2f(impulse).mul(invMass1)));
        body2.setLinearVelocity(new Vector2f(body2.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass2)));

    }

    public void addRigidBody(RigidBody2D rigidBody2D, boolean addGravity) {
        this.rigidBodies.add(rigidBody2D);
        if (addGravity)
            this.forceRegistry.add(gravity2D, rigidBody2D);

    }

}
