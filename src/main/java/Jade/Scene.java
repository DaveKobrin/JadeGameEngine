package Jade;

import Renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    public Scene(){}

    protected Renderer renderer;
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();


    public abstract void update(float dt);

    public void init() {
        /* Base does no initialization...
         * subclasses should override with any init code
         */
    }

    public void start() {
        this.isRunning = true;
        for (GameObject go : gameObjects) {
            go.start();
        }
    }

    public void addGameObject2Scene(GameObject go) {
        this.gameObjects.add(go);
        this.renderer.add(go);

        if (isRunning) {
            go.start();
        }
    }

    public Camera getCamera() {
        return this.camera;
    }
}
