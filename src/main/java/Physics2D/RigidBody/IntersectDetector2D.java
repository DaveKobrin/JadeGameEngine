package Physics2D.RigidBody;

import Physics2D.Primitives.*;
import Renderer.Line2D;
import Utility.JMath;
import org.joml.Vector2f;

public class IntersectDetector2D {

//===================================================================================
//                          Point vs Primitive tests
//===================================================================================

    public static boolean pointOnLine( final Vector2f point, final Line2D line) {
        //if point is an endpoint of this line
        if (point.equals(line.getStart()) || point.equals(line.getEnd()))
            return true;

        // line equation ( y= m*x + b)
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;
        if (dx == 0.0f)     // vertical line
            return point.x == line.getStart().x;
        float m = dy / dx;                                      //slope of the line
        float b = line.getEnd().y - ( m * line.getEnd().x);     //y intercept

        //check point is on the line
        boolean result = JMath.compare(m * point.x + b, point.y);
        return result;
    }

    public static boolean pointInCircle(final Vector2f point, final Circle circle) {
        //true if length from center to point < radius
        Vector2f center = circle.getRigidBody().getPosition();
        Vector2f center2point = new Vector2f(point).sub(center);    //offset from circle center to point

        //check square of length for performance
        return center2point.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInAABB(final Vector2f point, final AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        //true if min.x <= point.x <= max.x & min.y <= point.y <= max.y
        return min.x <= point.x && point.x <= max.x &&
               min.y <= point.y && point.y <= max.y;
    }

    public static boolean pointInBox2D(final Vector2f point, final Box2D box) {
        Vector2f min = box.getLocalMin();
        Vector2f max = box.getLocalMax();

        Vector2f pointLocal = new Vector2f(point);
        JMath.rotate(pointLocal, -box.getRigidBody().getRotation(), box.getRigidBody().getPosition());

        //true if min.x <= point.x <= max.x & min.y <= point.y <= max.y
        return min.x <= pointLocal.x && pointLocal.x <= max.x &&
                min.y <= pointLocal.y && pointLocal.y <= max.y;
    }

//===================================================================================
//                          Line vs Primitive tests
//===================================================================================

    public static boolean lineAndCircle(final Line2D line, final Circle circle) {
        //  if either end of the line segment are in the circle return true
        if (pointInCircle(line.getStart(), circle) || pointInCircle(line.getEnd(), circle))
            return true;

        //check projection of circle center onto line
        Vector2f segment = new Vector2f(line.getEnd().sub(line.getStart()));

        //calculate distance along segment to projection point (t)
        Vector2f lineStart2circleCenter = new Vector2f(circle.getRigidBody().getPosition().sub(line.getStart()));
        float t = lineStart2circleCenter.dot(segment) / segment.dot(segment);

        if ( t < 0.0f || t > 1.0f ) //projection is beyond the endpoints of the segment
            return false;

        //projection point
        Vector2f projection = new Vector2f(line.getStart().add(segment.mul(t)));

        return pointInCircle(projection, circle);
    }

    public static boolean lineAndAABB(final Line2D line, final AABB box) {
        if (pointInAABB(line.getStart(), box) || pointInAABB(line.getEnd(), box))
            return true;            //if either end is in the box return true immediately

        //build unit vector of line
        Vector2f unitVector = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0f) ? 1f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0f) ? 1f / unitVector.y : 0f;

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        min.sub(line.getStart()).mul(unitVector);
        max.sub(line.getStart()).mul(unitVector);

        float tmin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tmax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));

        if (tmax < 0 || tmin > tmax)
            return false;

        float t  = (tmin < 0) ? tmax : tmin;
        return t > 0 && t * t < line.getLengthSquared();

    }

    public static boolean lineAndBox2D(final Line2D line, final Box2D box) {
        float angle = box.getRigidBody().getRotation();
        Vector2f center = box.getRigidBody().getPosition();

        // create vectors for line endpoints in box local space
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());
        JMath.rotate(localStart, -angle, center);
        JMath.rotate(localEnd, -angle, center);

        // create new params for lineAndAABB
        Line2D localLine = new Line2D(localStart,localEnd);
        AABB aabb = new AABB(box.getLocalMin(), box.getLocalMax());

        return lineAndAABB(localLine, aabb);

    }

//===================================================================================
//                          Ray Casts
//===================================================================================
    public static boolean raycast(final Circle circle, final Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);
        Vector2f origin2Circle = new Vector2f(circle.getRigidBody().getPosition()).sub(ray.getOrigin());
        float radiusSq = circle.getRadius() * circle.getRadius();
        float origin2CircleLenSq = origin2Circle.lengthSquared();

        //  project origin2Circle onto ray
        float a = origin2Circle.dot(ray.getDirection());    //length of projection onto ray
        float bSq = origin2CircleLenSq - (a * a);           //distance between center of circle and projection

        if (radiusSq - bSq < 0.0f)          // if bSq > radiusSq then the projection falls outside the circle so no hit
            return false;

        // hit is detected. find out exactly where
        float f = (float) Math.sqrt(radiusSq - bSq);        //length along ray between hit location and projection

        float t;                                            //length from origin of ray to intersection point
        if (origin2CircleLenSq < radiusSq)
            t = a + f;                                      //if origin is inside circle t = a + f
        else
            t = a - f;                                      //origin outside circle t = a - f

        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getRigidBody().getPosition());
            normal.normalize();

            result.set(point, normal, t,true);
        }

        return true;

    }

    public static boolean raycast(final AABB box, final Ray2D ray, RaycastResult result) {

        RaycastResult.reset(result);

        //build unit vector of line
        Vector2f unitVector = ray.getDirection();
        unitVector.x = (unitVector.x != 0f) ? 1f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0f) ? 1f / unitVector.y : 0f;

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        min.sub(ray.getOrigin()).mul(unitVector);
        max.sub(ray.getOrigin()).mul(unitVector);

        float tmin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tmax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));

        if (tmax < 0 || tmin > tmax)
            return false;

        float t  = (tmin < 0) ? tmax : tmin;
        if ( t > 0 ) {//&& t < ray.getMaxLen();
            // hit detected
            if (result != null) {
                Vector2f point = new Vector2f(ray.getOrigin()).add(ray.getDirection().mul(t));
                Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
                normal.normalize();

                result.set(point, normal, t, true);
            }

            return true;
        }
        return false;
    }

    public static boolean raycast(final Box2D box, final Ray2D ray, RaycastResult result) {

        RaycastResult.reset(result);

        Vector2f localXAxis = new Vector2f(1f,0f);
        Vector2f localYAxis = new Vector2f(0f,1f);
        JMath.rotate(localXAxis, box.getRigidBody().getRotation(), new Vector2f(0f,0f));
        JMath.rotate(localYAxis, box.getRigidBody().getRotation(), new Vector2f(0f,0f));

        // vector p from ray origin to box center
        Vector2f p = new Vector2f(box.getRigidBody().getPosition()).sub(ray.getOrigin());
        // project ray direction onto box axis
        Vector2f proDirection = new Vector2f( localXAxis.dot(ray.getDirection()), localYAxis.dot(ray.getDirection()));
        // project p onto box axis
        Vector2f proP = new Vector2f(localXAxis.dot(p), localXAxis.dot(p));

        float[] tArray = { 0f, 0f, 0f, 0f };        // hold minX, maxX, minY, maxY

        for (int i = 0; i < 2; i++) {
            if (JMath.compare(proDirection.get(i), 0f)) {
                // if proDirection is 0 ray is parallel and if proP not within box will not hit
                if ((-proP.get(i) - box.getHalfSize().get(i) > 0f) || (-proP.get(i) + box.getHalfSize().get(i) < 0f))
                    return false;
                //otherwise will hit
                proDirection.setComponent(i, Float.MIN_VALUE);   //prevent div by 0
            }

            tArray[i * 2 + 0] = (proP.get(i) - box.getHalfSize().get(i)) / proDirection.get(i); //tmin for this axis
            tArray[i * 2 + 1] = (proP.get(i) + box.getHalfSize().get(i)) / proDirection.get(i); //tmax for this axis
        }

        float tMin = Math.max(Math.min(tArray[0], tArray[1]), Math.min(tArray[2], tArray[3]));
        float tMax = Math.min(Math.max(tArray[0], tArray[1]), Math.max(tArray[2], tArray[3]));

        if (tMax < 0 || tMin > tMax)
            return false;

        float t  = (tMin < 0) ? tMax : tMin;
        if ( t > 0 ) {//&& t < ray.getMaxLen();
            // hit detected
            if (result != null) {
                Vector2f point = new Vector2f(ray.getOrigin()).add(ray.getDirection().mul(t));
                Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
                normal.normalize();

                result.set(point, normal, t, true);
            }

            return true;
        }
        return false;
    }

//===================================================================================
//                          Circle vs Primitives
//===================================================================================
    public static boolean circleAndLine(final Circle circle, final Line2D line) {
        return lineAndCircle(line, circle);
    }

    public static boolean circleAndCircle(final Circle circle1, final Circle circle2) {
        Vector2f vecBetweenCenters = new Vector2f(circle2.getRigidBody().getPosition()).sub(circle1.getRigidBody().getPosition());
        float sumOfRadii = circle1.getRadius() + circle2.getRadius();
        return vecBetweenCenters.lengthSquared() <= sumOfRadii * sumOfRadii;
    }

    public static boolean circleAndAABB(final Circle circle, final AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        // find closest point on the box to the center of the circle. will remain circle center if inside box
        Vector2f closestPointOnBox = new Vector2f(circle.getRigidBody().getPosition());

        if (closestPointOnBox.x < min.x)
            closestPointOnBox.x = min.x;

        if (closestPointOnBox.x > max.x)
            closestPointOnBox.x = max.x;

        if (closestPointOnBox.y < min.y)
            closestPointOnBox.y = min.y;

        if (closestPointOnBox.y > max.y)
            closestPointOnBox.y = max.y;

        Vector2f closest2Center = new Vector2f(circle.getRigidBody().getPosition()).sub(closestPointOnBox);
        return closest2Center.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean circleAndBox2D(final Circle circle, final Box2D box) {
        //  treat box as an AABB from (0,0) to box.getSize()
        Vector2f min = new Vector2f(0f,0f);
        Vector2f max = new Vector2f(box.getSize());

        Vector2f localPosOfCircle = new Vector2f(circle.getRigidBody().getPosition()).sub(box.getRigidBody().getPosition());
        JMath.rotate(localPosOfCircle, -box.getRigidBody().getRotation(),new Vector2f(0f,0f));
        localPosOfCircle.add(box.getHalfSize());

        // find closest point on the box to the center of the circle. will remain circle center if inside box
        Vector2f closestPointOnBox = new Vector2f(localPosOfCircle);

        if (closestPointOnBox.x < min.x)
            closestPointOnBox.x = min.x;

        if (closestPointOnBox.x > max.x)
            closestPointOnBox.x = max.x;

        if (closestPointOnBox.y < min.y)
            closestPointOnBox.y = min.y;

        if (closestPointOnBox.y > max.y)
            closestPointOnBox.y = max.y;

        Vector2f closest2Center = new Vector2f(localPosOfCircle).sub(closestPointOnBox);
        return closest2Center.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

//===================================================================================
//                          AABB vs Primitives
//===================================================================================


    public static boolean AABBAndCircle(final AABB box, final Circle circle) {
        return circleAndAABB(circle, box);
    }

    public static boolean AABBAndAABB(final AABB box1, final AABB box2) {
        Vector2f[] axes2Test = {new Vector2f(0f,1f), new Vector2f(1f,0f)};
        for (int i = 0; i < axes2Test.length; i++) {
            if (!overlapOnAxis(box1, box2, axes2Test[i]))
                return false;
        }
        return true;
    }

    public static boolean AABBAndBox2D(final AABB aabb, final Box2D box2D) {
        Vector2f[] axes2Test = {
                new Vector2f(1f,0f),        //aabb x-axis
                new Vector2f(0f,1f),        //aabb y-axis
                new Vector2f(1f,0f),        //box2D x-axis
                new Vector2f(0f,1f)         //box2D y-axis
        };

        if (JMath.compare(box2D.getRigidBody().getRotation(), 0f)) {
            JMath.rotate(axes2Test[2], box2D.getRigidBody().getRotation(), new Vector2f(0f,0f));
            JMath.rotate(axes2Test[3], box2D.getRigidBody().getRotation(), new Vector2f(0f,0f));
        }

        for (int i = 0; i < axes2Test.length; i++) {
            if (!overlapOnAxis(aabb, box2D, axes2Test[i]))
                return false;
        }
        return true;
    }

//===================================================================================
//                          AABB vs Primitives
//===================================================================================

    public static boolean Box2DAndCircle(final Box2D box, final Circle circle) {
        return circleAndBox2D(circle, box);
    }

    public static boolean Box2DAndAABB(final Box2D box2D, final AABB aabb) {
        return AABBAndBox2D(aabb, box2D);
    }

    public static boolean Box2DAndBox2D(final Box2D box1, final Box2D box2) {
        Vector2f[] axes2Test = {
                new Vector2f(1f,0f),        //aabb x-axis
                new Vector2f(0f,1f),        //aabb y-axis
                new Vector2f(1f,0f),        //box2D x-axis
                new Vector2f(0f,1f)         //box2D y-axis
        };

        if (JMath.compare(box1.getRigidBody().getRotation(), 0f)) {
            JMath.rotate(axes2Test[2], box1.getRigidBody().getRotation(), new Vector2f(0f,0f));
            JMath.rotate(axes2Test[3], box1.getRigidBody().getRotation(), new Vector2f(0f,0f));
        }

        if (JMath.compare(box2.getRigidBody().getRotation(), 0f)) {
            JMath.rotate(axes2Test[2], box2.getRigidBody().getRotation(), new Vector2f(0f,0f));
            JMath.rotate(axes2Test[3], box2.getRigidBody().getRotation(), new Vector2f(0f,0f));
        }

        for (int i = 0; i < axes2Test.length; i++) {
            if (!overlapOnAxis(box1, box2, axes2Test[i]))
                return false;
        }
        return true;
    }

//--------------------------------------------------------------------------
//  Separated Axis Theory (SAT) helper functions
//--------------------------------------------------------------------------
    private static Vector2f getInterval(final AABB box, final Vector2f axis) {
        //returns min and max projections of box onto axis
        axis.normalize();       //ensure unit vector
        Vector2f[] vertices = box.getVertices();
        Vector2f result = new Vector2f(vertices[0].dot(axis), vertices[0].dot(axis));

        float tmp;
        for (Vector2f vert : vertices) {
            tmp = vert.dot(axis);
            if ( tmp < result.x)
                result.x = tmp;
            if (tmp > result.y)
                result.y = tmp;
        }
        return result;
    }

    private static Vector2f getInterval(final Box2D box, final Vector2f axis) {
        //returns min and max projections of box onto axis
        if (axis.lengthSquared() != 1f) axis.normalize();       //ensure unit vector
        Vector2f[] vertices = box.getVertices();
        Vector2f result = new Vector2f(vertices[0].dot(axis), vertices[0].dot(axis));

        float tmp = 0f;
        for (Vector2f vert : vertices) {
            tmp = vert.dot(axis);
            if ( tmp < result.x)
                result.x = tmp;
            if (tmp > result.y)
                result.y = tmp;
        }
        return result;
    }

    private static boolean overlapOnAxis(final AABB box1, final AABB box2, final Vector2f axis) {
        Vector2f intervalBox1 = new Vector2f(getInterval(box1, axis));
        Vector2f intervalBox2 = new Vector2f(getInterval(box2, axis));

        return ((intervalBox2.x <= intervalBox1.y) && (intervalBox1.x <= intervalBox2.y));
    }

    private static boolean overlapOnAxis(final AABB box1, final Box2D box2, final Vector2f axis) {
        Vector2f intervalBox1 = new Vector2f(getInterval(box1, axis));
        Vector2f intervalBox2 = new Vector2f(getInterval(box2, axis));

        return ((intervalBox2.x <= intervalBox1.y) && (intervalBox1.x <= intervalBox2.y));
    }

    private static boolean overlapOnAxis(final Box2D box1, final AABB box2, final Vector2f axis) {
        Vector2f intervalBox1 = new Vector2f(getInterval(box1, axis));
        Vector2f intervalBox2 = new Vector2f(getInterval(box2, axis));

        return ((intervalBox2.x <= intervalBox1.y) && (intervalBox1.x <= intervalBox2.y));
    }

    private static boolean overlapOnAxis(final Box2D box1, final Box2D box2, final Vector2f axis) {
        Vector2f intervalBox1 = new Vector2f(getInterval(box1, axis));
        Vector2f intervalBox2 = new Vector2f(getInterval(box2, axis));

        return ((intervalBox2.x <= intervalBox1.y) && (intervalBox1.x <= intervalBox2.y));
    }


}




