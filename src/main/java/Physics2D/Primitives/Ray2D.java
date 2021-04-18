package Physics2D.Primitives;

import lombok.Getter;
import org.joml.Vector2f;

public class Ray2D {
    @Getter
    private Vector2f origin;
    @Getter
    private Vector2f direction;

    public Ray2D(Vector2f origin, Vector2f direction) {
        this.origin = new Vector2f(origin);
        this.direction = new Vector2f(direction);
        this.direction.normalize();
    }
}
