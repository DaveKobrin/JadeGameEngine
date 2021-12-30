package scenes;

import Components.MouseControl;
import Components.SpriteRenderer;
import Editor.GameViewWindow;
import Jade.Camera;
import Jade.Component;
import Jade.GameObject;
import Jade.Transform;
import Renderer.Renderer;
import Utility.AssetPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Scene - Parent class for a level. Everything that exists in the game world
 *         is part of the scene all of the GameObjects, camera, renderer, etc.
 *         Children may be playable levels, or the level editor for example.
 */
public abstract class Scene {

    public Scene(){}

    protected Renderer renderer;
    protected Camera camera;
    private boolean isRunning = false;

    protected boolean levelLoaded = false;
    @Getter     @Setter
    protected String levelFileName = "";
    protected List<GameObject> gameObjects = new ArrayList<>();
    @Getter
    protected GameViewWindow gameViewWindow = new GameViewWindow();

    /**
     * update(dt) - called every frame and in turn calls update(dt) on all
     *              GameObjects.
     * @param dt - (delta time) - float representing milliseconds elapsed since last
     *             frame.
     */
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

    public void render() {
        this.renderer.render();
    }
    /**
     * init() - initialize variables - children may override fore specific needs, but must
     *          create these instances or call back to this method
     */
    public void init() {
        this.camera = new Camera(new Vector2f());
        this.renderer = new Renderer();
        loadResources();
    }

    /**
     * start() - change game state to running and call the start() method for all
     *           GameObjects.
     */
    public void start() {
        this.isRunning = true;
        for (GameObject go : gameObjects) {
            go.start();
        }
    }

    /**
     * loadResources() - add all assets for the scene to the asset pool.
     *                   Should be overridden by child classes if assets are used.
     */
    protected void loadResources() {
        //override with scene specific resources
    }

    /**
     * addGameObject2Scene(go) - adds the GameObject into the scene and calls its start()
     *                           if needed.
     * @param go - the GameObject to add.
     */
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



    /**
     * imGui() - process UI items specific to the scene
     */
    public void imGui() {
        /* Base class does nothing here.
         * subclasses can override for
         * scene level gui code
         */
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.setTransform(go.getComponent(Transform.class));
        return go;
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(levelFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {

            int maxGoUID = -1;
            int maxCompUID = -1;

            GameObject[] objects = gson.fromJson(inFile,GameObject[].class);
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getComponent(SpriteRenderer.class) != null) {
                    SpriteRenderer spriteRen = objects[i].getComponent(SpriteRenderer.class);
                    if (spriteRen.getTexture() != null) {
                        spriteRen.setTexture(AssetPool.getTexture(spriteRen.getTexture().getFilepath()));
                    }
                }
                addGameObject2Scene(objects[i]);

                if (objects[i].getUid() > maxGoUID)
                    maxGoUID = objects[i].getUid();
                if (objects[i].getMaxCompUID() > maxCompUID)
                    maxCompUID = objects[i].getUid();
            }

            GameObject.setIdCounter(++maxGoUID);
            Component.setIdCounter(++maxCompUID);

            this.levelLoaded = true;
        }
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        List<GameObject> objects2Serialize = new ArrayList<>();
        for (GameObject obj : this.gameObjects) {
            if (obj.isDoSerialize())
                objects2Serialize.add(obj);
        }

        try (FileWriter outfile = new FileWriter(new File(levelFileName),false)){
           outfile.write(gson.toJson(objects2Serialize));
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public GameObject getGoByUID(int uid) {
        //returns the gameObject with the uid or null if not found
    for (GameObject go : gameObjects) {
            if (go.getUid() == uid)
                return go;
        }
        return null;
    }

}
