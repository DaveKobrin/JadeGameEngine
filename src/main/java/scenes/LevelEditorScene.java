package scenes;


import Components.*;
import Jade.GameObject;
import Jade.PreFabs;
import Jade.Transform;
import Jade.Window;
import Physics2D.PhysicsSystem2D;
import Physics2D.Primitives.Box2D;
import Physics2D.Primitives.Circle;
import Physics2D.RigidBody.RigidBody2D;
import Renderer.DebugDrawBatch;
import Utility.AssetPool;
import Utility.Color;
import Utility.Settings;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

/**
 * LevelEditorScene - Scene for the game level editor - currently used for test code
 */
public class LevelEditorScene extends Scene {

    private Spritesheet blocks = null;
    private Spritesheet gizmos = null;
    private GameObject LevelEditorComponents = this.createGameObject("EditorComponents");
// ------- test code ---------------
    Box2D box2D = new Box2D(new Vector2f(475f,210f), new Vector2f(625f,250f), 0f);
    Circle c1 = new Circle();
    Circle c2 = new Circle();
    float rotation = 0f;

    PhysicsSystem2D physicsSystem2D = new PhysicsSystem2D(1f/60f, new Vector2f(0f,-9.8f));
    Transform obj1,obj2;
    RigidBody2D rb1, rb2;
//----------------------------------

    public LevelEditorScene() {
        setLevelFileName("level.txt");
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        this.camera.adjustProjection();
        LevelEditorComponents.update(dt);
        //System.out.println( "FPS: " + (1.0f/dt));
//---------test code ----------------
//        rotation += 50f * dt;
//        rotation %= 360;
//        box2D.getRigidBody().setRotation(rotation);
//        boolean hit = IntersectDetector2D.circleAndBox2D(circle,box2D);
//        DebugDrawBatch.addLine2D(new Vector2f(0.0f, 0.0f), new Vector2f(800.0f,800.0f), new Color(Color.COLORS.RED), 240);
//        DebugDrawBatch.addBox2D(box2D.getRigidBody().getPosition(), box2D.getSize(), box2D.getRigidBody().getRotation(), hit ? Color.COLORS.RED.getAsColor() : Color.COLORS.GREEN.getAsColor(), 1);
//        DebugDrawBatch.addCircle(circle.getRigidBody().getPosition(), circle.getRadius(),  hit ? Color.COLORS.RED.getAsColor() : Color.COLORS.GREEN.getAsColor(), 1);

        DebugDrawBatch.addCircle(obj1.getPosition(), c1.getRadius(), Color.COLORS.RED.getAsColor());
        DebugDrawBatch.addCircle(obj2.getPosition(), c2.getRadius(), Color.COLORS.BLUE.getAsColor());

//        Vector2f vpPos = new Vector2f(GameViewWindow.getViewportPosX(), GameViewWindow.getViewportPosY());
//        Vector2f vpSize = new Vector2f(GameViewWindow.getViewportSizeX(), GameViewWindow.getViewportSizeY());
//
//        Vector2f viewportCenter = new Vector2f(vpPos.x + vpSize.x/2f,vpPos.y + vpSize.y/2f);
//
//        DebugDrawBatch.addBox2D(viewportCenter, vpSize, Color.COLORS.YELLOW.getAsColor());
        physicsSystem2D.update(dt);

//------------------------------------
    }

    @Override
    public void init() {
        super.init();
        this.load();


        LevelEditorComponents.addComponent(new MouseControl());
        LevelEditorComponents.addComponent(new GridLines());
        LevelEditorComponents.addComponent(new EditorCamera(this.getCamera()));
        LevelEditorComponents.addComponent(new GizmoSystem(gizmos));
        LevelEditorComponents.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.get().getImGuiLayer().getPropertiesWindow()));
        LevelEditorComponents.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.get().getImGuiLayer().getPropertiesWindow()));
        LevelEditorComponents.start();

        //=====================================================
        //          test code
        //=====================================================
        obj1 = new Transform(new Vector2f(200,600), new Vector2f(1,1));
        obj2 = new Transform(new Vector2f(201,700), new Vector2f(1,1));
        rb1 = new RigidBody2D();
        rb2 = new RigidBody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        rb1.setMass(100f);
        rb2.setMass(200f);
        c1.setRadius(16f);
        c2.setRadius(16f);
        c1.setRigidBody(rb1);
        c2.setRigidBody(rb2);
        rb1.setCollider(c1);
        rb2.setCollider(c2);
        physicsSystem2D.addRigidBody(rb1, false);
        physicsSystem2D.addRigidBody(rb2, true);

        //-----------------------------------------------------

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
        */
//        if (!this.levelLoaded) {
//            GameObject go = new GameObject("Square", new Transform(new Vector2f(500.0f, 400.0f), new Vector2f(128.0f, 128.0f)), 0);
//            SpriteRenderer goSpriteRenderer = new SpriteRenderer();
//            goSpriteRenderer.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
//            go.addComponent(goSpriteRenderer);
//            go.addComponent(new RigidBody2D());
//            this.addGameObject2Scene(go);
//            this.activeGameObject = go;
//
//            Gson gson = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create();
//
//            String jsonout = gson.toJson(go);
//            System.out.println(jsonout);
//
//            GameObject go2 = gson.fromJson(jsonout, GameObject.class);
//            go2.getTransform().setPosition(new Vector2f(600.0f, 400.0f));
//            go2.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
//
//            go2.getComponent(SpriteRenderer.class).setSprite(AssetPool.getSpritesheet("assets/textures/decorationsAndBlocks.png").getSprite(0));
//
//            this.addGameObject2Scene(go2);
//        } else {
//            this.activeGameObject = this.gameObjects.get(0);
//        }

    }

    @Override
    protected void loadResources() {
        AssetPool.addShaderResource("assets/shaders/default.glsl");
        AssetPool.addShaderResource("assets/shaders/debug_line2d.glsl");
        AssetPool.addShaderResource("assets/shaders/picking_shader.glsl");
        AssetPool.addTextureResource("assets/textures/decorationsAndBlocks.png");
        AssetPool.addTextureResource("assets/textures/gizmos.png");
        AssetPool.addSpritesheetResource("assets/textures/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/textures/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.addSpritesheetResource("assets/textures/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/textures/gizmos.png"),
                        24,48,3,0));
        blocks = AssetPool.getSpritesheet("assets/textures/decorationsAndBlocks.png");
        gizmos = AssetPool.getSpritesheet("assets/textures/gizmos.png");
    }

    @Override
    public void imGui() {
        ImGui.begin("Level Editor Components");
        {
            LevelEditorComponents.imGui();
        }
        ImGui.end();

        ImGui.begin("Test Window");
        {
            // window information
            ImVec2 windowPos = new ImVec2();
            ImVec2 windowSize = new ImVec2();
            ImVec2 itemSpacing = new ImVec2();
            ImGui.getWindowPos(windowPos);
            ImGui.getWindowSize(windowSize);
            ImGui.getStyle().getItemSpacing(itemSpacing);
            float windowX2 = windowPos.x + windowSize.x;


            // adding items into the window
            for (int i = 0; i < blocks.getSize(); i++) {
                Sprite sprite = blocks.getSprite(i);
                Vector2f buttonSize = new Vector2f(sprite.getWidth() * 2, sprite.getHeight() * 2);
                int texID = sprite.getTexture().getTexID();
                Vector2f[] texUV = sprite.getTexCoords();

                ImGui.pushID(i);
                if (ImGui.imageButton(texID, buttonSize.x, buttonSize.y, texUV[3].x, texUV[3].y, texUV[1].x, texUV[1].y)) {
                    GameObject object = PreFabs.generateBlock(sprite, Settings.TILE_WIDTH, Settings.TILE_HEIGHT);
                    LevelEditorComponents.getComponent(MouseControl.class).attach(object);
                }
                ImGui.popID();


                ImVec2 lastButtonPos = new ImVec2();
                ImGui.getItemRectMax(lastButtonPos);
                float nextButtonX2 = lastButtonPos.x + itemSpacing.x + buttonSize.x;
                if (i + 1 < blocks.getSize() && nextButtonX2 < windowX2) {
                    ImGui.sameLine();
                }
            }
        }
        ImGui.end();
    }

}
