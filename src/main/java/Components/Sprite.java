package Components;

import Renderer.Texture;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

/**
 * Sprite - Simple 2D sprite that holds its texture and UV coordinates
 */
@Getter
@Setter
public class Sprite {
    private Texture texture = null;
    private float width = 0;
    private float height =0;
    private Vector2f[] texCoords = {
            new Vector2f(1.0f,1.0f),
            new Vector2f(1.0f,0.0f),
            new Vector2f(0.0f,0.0f),
            new Vector2f(0.0f,1.0f)
    };
}
