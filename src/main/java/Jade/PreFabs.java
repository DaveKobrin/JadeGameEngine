package Jade;

import Components.Sprite;
import Components.SpriteRenderer;
import org.joml.Vector2f;

public class PreFabs {

    public static GameObject generateBlock(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Block_Gen");
        block.getTransform().setScale(new Vector2f(sizeX, sizeY));
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        block.addComponent(spriteRenderer);
        return block;
    }

    public static GameObject generateGizmo(Sprite sprite, float sizeX, float sizeY) {
        GameObject gizmo = Window.getScene().createGameObject("Gizmo_Gen");
        gizmo.getTransform().setScale( new Vector2f(sizeX, sizeY));
        gizmo.getTransform().setZIndex(20);
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        gizmo.addComponent(spriteRenderer);
        return gizmo;
    }
}
