package Renderer;

import Components.SpriteRenderer;
import Jade.GameObject;
import Utility.AssetPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Renderer - contains and manages all the RenderBatches
 */
public class Renderer {
    private static Shader currShader = AssetPool.getShader("assets/shaders/default.glsl");
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public static void bindShader(Shader shader) { currShader = shader; }
    public static Shader getCurrShader() { return currShader; }

    public Renderer () { this.batches = new ArrayList<>(); }

    public void add(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        if (sprite != null) {
            add(sprite);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.getzIndex() == sprite.gameObject.getTransform().getZIndex() && batch.hasRoom() && batch.hasTexRoom(sprite.getTexture())) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getTransform().getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
