package Physics2D.Primitives;

import Physics2D.RigidBody.RigidBody2D;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

public class Circle extends Collider2D{

    @Setter @Getter
    private float radius = 1.0f;
    @Setter @Getter
    private RigidBody2D rigidBody = new RigidBody2D();

    public Circle(){ }
    public Circle(Vector2f center, float radius){
        this.rigidBody.setPosition(center);
        this.radius = radius;
    }

}
