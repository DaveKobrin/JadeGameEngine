import Physics2D.Primitives.AABB;
import Physics2D.Primitives.Box2D;
import Physics2D.Primitives.Circle;
import Physics2D.RigidBody.IntersectDetector2D;
import Renderer.Line2D;
import org.joml.Vector2f;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Physics2D_Tests {

    @ParameterizedTest
    @CsvSource({
            //point     line start end  expected
            "0,0,       0,0,    5,12,   true",
            "5,12,      0,0,    5,12,   true",
            "0,1,       0,0,    5,12,   false",
            "5,5,       6,5,    12,5,   true",
            "5,5,       5,0,    5,12,   true"
    })

    void IntersectDetector_pointOnLine(float pX,float pY, float startX, float startY, float endX, float endY, boolean expected) {
        Line2D line = new Line2D(new Vector2f(startX,startY), new Vector2f(endX,endY));
        Vector2f point = new Vector2f(pX,pY);
        boolean result = IntersectDetector2D.pointOnLine(point, line);
//        System.out.println( result + "  " + expected);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            //point         center radius  expected
            "0,5.0001f,     0,0,    5,      false",
            "-1,-4,         0,0,    5,      true",
            "5,12,          0,0,    5,      false",
            "0,1,           0,0,    0.5f,   false",
            "5,5,           6,5,    3,      true",
            "8,9,           5,6,    3,      false"
    })

    void IntersectDetector_pointInCircle(float pX,float pY, float centerX, float centerY, float radius,  boolean expected) {
        Circle circle = new Circle(new Vector2f(centerX, centerY), radius );
        Vector2f point = new Vector2f(pX,pY);
        boolean result = IntersectDetector2D.pointInCircle(point, circle);
//        System.out.println( result + "  " + expected);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            //point     AABB min max  expected
            "0,0,       0,0,    5,12,   true",
            "5,12,      0,0,    5,12,   true",
            "1,1,       0,0,    5,12,   true",
            "5,5,       6,5,    12,5,   false",
            "5,5,       5,0,    5,12,   true"
    })

    void IntersectDetector_pointInAABB(float pX,float pY, float minX, float minY, float maxX, float maxY,  boolean expected) {
        AABB box = new AABB(new Vector2f(minX, minY), new Vector2f(maxX, maxY));
        Vector2f point = new Vector2f(pX,pY);
        boolean result = IntersectDetector2D.pointInAABB(point, box);
//        System.out.println( result + "  " + expected);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            //point         Box2D min max rotation      expected
            "0,0,           -2,-1,    2,1,  45,         true",
            "2,0,           -2,-1,    2,1,  45,         false",
            "1.4f,1.4f,     -2,-1,    2,1,  45,         true",
            "0,2,           -2,-1,    2,1,  90,         true",
            "6,9,           5,6,      7,15, 30,         true"
    })

    void IntersectDetector_pointInBox2D(float pX,float pY, float minX, float minY, float maxX, float maxY, float rotation, boolean expected) {
        Box2D box = new Box2D(new Vector2f(minX, minY), new Vector2f(maxX, maxY), rotation);
        Vector2f point = new Vector2f(pX,pY);
        boolean result = IntersectDetector2D.pointInBox2D(point, box);
//        System.out.println( result + "  " + expected);
        assertEquals(expected, result);
    }
}