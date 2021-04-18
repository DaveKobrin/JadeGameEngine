package Physics2D.RigidBody;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    @Getter @Setter
    private boolean isColliding = false;
    @Getter @Setter
    private Vector2f collNormal = new Vector2f();
    @Getter
    private List<Vector2f> collisionPoints = new ArrayList<>();
    @Getter @Setter
    private float collDepth = 0f;

    public CollisionManifold(){}

    public CollisionManifold(Vector2f collNormal, float collDepth) {
        this.collNormal = collNormal;
        this.collDepth = collDepth;
        this.isColliding = true;
    }

    public void addCollisionPoint(Vector2f point) {
        this.collisionPoints.add(point);
    }
}
