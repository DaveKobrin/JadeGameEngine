package Physics2D.Primitives;

import lombok.Getter;
import org.joml.Vector2f;

public class RaycastResult {
    @Getter private Vector2f point = new Vector2f();
    @Getter private Vector2f normal = new Vector2f();
    @Getter private float t = -1;
    @Getter private boolean hit = false;

    public void set(Vector2f point, Vector2f normal, float t, boolean hit){
        this.point.set(point);
        this.normal.set(normal);
        this.t = t;
        this.hit = hit;
    }

    public static void reset(RaycastResult result) {
        if (result != null) {
            result.point.zero();
            result.normal.set(0.0f, 0.0f);
            result.t = -1;
            result.hit = false;
        }
    }
}
