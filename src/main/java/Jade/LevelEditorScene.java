package Jade;


import Components.Sprite;
import Components.SpriteRenderer;
import Renderer.Renderer;
import Renderer.Texture;
import Utility.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene{

    public LevelEditorScene() {

    }

    @Override
    public void update(float dt) {
        // TODO move this loop to Scene.update and call super version here

        //System.out.println( "FPS: " + (1.0f/dt));

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        this.renderer.render();
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        this.renderer = new Renderer();
        loadResources();

/*        int xOff = 50;
        int yOff = 50;

        float totalWidth = (float)(600 - xOff * 2);
        float totalHeight = (float)(300 - yOff * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOff + (x * sizeX);
                float yPos = yOff + (y * sizeY);

                GameObject go = new GameObject("Obj"+x+","+y, new Transform(new Vector2f(xPos,yPos),new Vector2f(sizeX,sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos/totalWidth,yPos/totalHeight,1,1)));
                this.addGameObject2Scene(go);
            }
        }
        GameObject go = new GameObject("PacTest", new Transform(new Vector2f(500.0f,400.0f),new Vector2f(32.0f,32.0f)));
        go.addComponent(new SpriteRenderer(new Sprite(new Texture("assets/textures/pacman_walk_frame0.png"))));
        this.addGameObject2Scene(go);

 */
    }

    private void loadResources() {
        AssetPool.addShaderResource("assets/shaders/default.glsl");
        AssetPool.addTextureResource("assets/textures/pacman_walk_frame0.png");
    }
}
