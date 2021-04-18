package Physics2D.RigidBody;

import Physics2D.Primitives.Circle;
import Physics2D.Primitives.Collider2D;
import org.joml.Vector2f;

public class Collisions2D {

    public static CollisionManifold findCollisionFeatures(Collider2D c1, Collider2D c2){
        //TODO : fix this mess consider Utility.TypeClassMap
        Class class1 = null;
        Class class2 = null;

        //find class of c1
        if (c1 instanceof Circle) {
            class1 = Circle.class;
        }
        //find class of c2
        if (c2 instanceof Circle) {
            class2 = Circle.class;
        }

        assert class1 != null && class2 != null : "Unknown Collider2D c1: " + c1.getClass() + "   c2: " + c2.getClass();

        return findCollisionFeatures(((Circle) c1), ((Circle) c2));
    }

//    public static CollisionManifold findCollisionFeatures(Object c1, Object c2){
//        assert false : "Unknown Collider2d... should never get here!";
//        return null;
//    }
    public static CollisionManifold findCollisionFeatures(Circle c1, Circle c2) {
        CollisionManifold result = new CollisionManifold();
        float sumRadii = c1.getRadius() + c2.getRadius();
        Vector2f distanceBetween = new Vector2f(c2.getRigidBody().getPosition()).sub(c1.getRigidBody().getPosition());

        if ((distanceBetween.lengthSquared() - sumRadii * sumRadii) > 0f)
            return result;  // not colliding

        // find depth of intersection. mult by .5 to adjust position of both circles the same
        // todo: consider momentum, and velocity in this
        float depth = Math.abs(distanceBetween.length() - sumRadii) * 0.5f;
        Vector2f normal = new Vector2f(distanceBetween).normalize();
        float distance2Point = c1.getRadius() - depth;
        Vector2f contactPoint = new Vector2f(c1.getRigidBody().getPosition()).add(new Vector2f(normal).mul(distance2Point));
        result.setCollNormal(normal);
        result.setCollDepth(depth);
        result.setColliding(true);
        result.addCollisionPoint(contactPoint);

        return result;
    }
}
