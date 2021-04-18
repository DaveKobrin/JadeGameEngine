package Jade;

import Components.Sprite;
import Components.SpriteRenderer;
import org.joml.Vector2f;

public class PreFabs {

    public static GameObject generateBlock(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = new GameObject("Block_Gen", new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)),0);
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        block.addComponent(spriteRenderer);
        return block;
    }
}
