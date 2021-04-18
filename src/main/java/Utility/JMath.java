package Utility;

import org.joml.Vector2f;

public class JMath {

    /**
     * rotate - rotate a Vector2f by angleDeg degrees around the rotationCenter will modify point
     * @param point - Vector2f point to rotate AND output location WILL OVERWRITE
     * @param angleDeg - float rotation amount in degrees
     * @param rotationCenter - Vector2f point to center rotation around
     */
    public static void rotate(Vector2f point, float angleDeg, Vector2f rotationCenter) {
        float x = point.x - rotationCenter.x;
        float y = point.y - rotationCenter.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        point.x = xPrime + rotationCenter.x;
        point.y = yPrime + rotationCenter.y;
    }

    public static boolean compare(float x, float y, float threshold) {
        return Math.abs( x - y ) <= threshold * Math.max(1.0f, Math.max(Math.abs(x),Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float threshold) {
        return compare(vec1.x, vec2.x, threshold) && compare(vec1.y, vec2.y, threshold);
    }

    public static boolean compare(float x, float y) {
        return Math.abs( x - y ) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x),Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
}
