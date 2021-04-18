package Components;

import Renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Spritesheet - allows for multiple sprites to be contained in a single Texture
 *               manages the array of sprites by UV coordinates within that Texture
 */
public class Spritesheet {
    private Texture texture;
    private List<Sprite> sprites;

    public Spritesheet (Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing ) {
        this.sprites = new ArrayList<>();
        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        for (int i = 0; i < numSprites; i++) {
            float top = ((float) (currentY + spriteHeight)) / (float) texture.getHeight();
            float bottom = ((float) currentY) / (float) texture.getHeight();
            float left = ((float) currentX) / (float) texture.getWidth();
            float right = ((float) (currentX + spriteWidth)) / (float) texture.getWidth();

            Vector2f[] texCoords = {
                    new Vector2f(right, top),
                    new Vector2f(right, bottom),
                    new Vector2f(left, bottom),
                    new Vector2f(left, top)
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

    public int getSize() {
        return sprites.size();
    }
}
