package Jade;

import Renderer.DebugDrawBatch;
import Renderer.Framebuffer;
import Renderer.PickingTexture;
import Renderer.Renderer;
import Utility.AssetPool;
import Utility.Color;
import lombok.Data;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Window - creates and manages the single instance of the openGL window
 */
public class Window {
    @Data
    public class Configuration {
        /**
         *  Window Title
         */
        private String title = "Balls2D";

        /**
         *  Window Width and Height
         */
        private int width = 1600;
        private int height = 900;

        /**
         *  Window state flags
         */
        private boolean fullScreen = false;

    }

    private Configuration config;
    private Color colorBg = Color.COLORS.MID_GRAY.getAsColor();

    //used to calculate delta time (dt) - elapsed time between frames in millis
    private float beginTime;
    private float endTime;
    private float dt;

    //window instance and handle
    private static Window window = null;
    private long glfwWindow;

    //imgui
    private String glslVersion = null;
    @Getter
    private ImGuiLayer imGuiLayer;

    private static Scene currentScene = null;
    @Getter
    private Framebuffer framebuffer;

    @Getter
    private PickingTexture pickingTexture;

    private Window() {
        config = new Configuration();
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
//        currentScene.load();
        currentScene.init();
        currentScene.start();
    }
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return currentScene;
    }

    /**
     * run() - run the game and free the resources after
     */
    public void run(){
//        System.out.println("hello lwjgl" + Version.getVersion() + "!");

        init();
        loop();
        dispose();
    }

    private void dispose() {
        imGuiLayer.dispose();

        //free allocated memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate the window and free the error callback
        glfwTerminate();
        try {
            glfwSetErrorCallback(null).free();
        } catch (NullPointerException e) {
            e.printStackTrace();
            assert false : "Could not free glfw error callback.";
        }
    }

    /**
     * init() - initialize window, GLFW, imGui, and set to the first scene
     */
    public void init() {
        //setup lwjgl error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        decideGlGlslVersions();

        //configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, (config.isFullScreen() ? GLFW_TRUE:GLFW_FALSE));

        //create the window
        glfwWindow = glfwCreateWindow(config.getWidth(), config.getHeight(), config.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            org.lwjgl.glfw.GLFW.glfwGetWindowSize(glfwWindow, pWidth, pHeight);
            final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
            GLFW.glfwSetWindowPos(glfwWindow, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync
        glfwSwapInterval(1);

        //make the window visible
        glfwShowWindow(glfwWindow);

        //This line is critical for LWJGL's interoperation with GLFW's
        //OpenGL context, or any context that is managed externally.
        //LWJGL detects the context that is currentin the current thread,
        //creates the GLCapabilities instance and makes the OpenGL
        //bindings available for use.
        GL.createCapabilities();

        imGuiLayer = new ImGuiLayer();
        imGuiLayer.init(glslVersion);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer  = new Framebuffer(getWidth(), getHeight());
        this.pickingTexture = new PickingTexture(getWidth(), getHeight());
        glViewport(0, 0, getWidth(), getHeight());

        changeScene(0);
    }

    private void decideGlGlslVersions() {
        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMac) {
            glslVersion = "#version 150";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);          // Required on Mac
        } else {
            glslVersion = "#version 130";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
        }
    }

    /**
     * loop() - game loop - calc dt, and update all objects each frame
     */
    public void loop() {
        beginTime = (float) glfwGetTime() ;
        endTime = (float) glfwGetTime();
        dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            //poll events
            glfwPollEvents();
            //render pass 1 render to pickingTexture
            glDisable(GL_BLEND);
            pickingTexture.enableWrite();
            glViewport(0,0, getWidth(), getHeight());
            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(AssetPool.getShader("assets/shaders/picking_shader.glsl"));
            currentScene.render();

            pickingTexture.disableWrite();
            glEnable(GL_BLEND);


            //render pass 2
            this.framebuffer.bind();    //render scene to framebuffer

            glClearColor(colorBg.getRed(),colorBg.getGreen(),colorBg.getBlue(),colorBg.getAlpha());
            glClear(GL_COLOR_BUFFER_BIT);

            DebugDrawBatch.beginFrame();


            if ( dt >= 0.0f) {
                DebugDrawBatch.draw();
                currentScene.update(dt);
                Renderer.bindShader(AssetPool.getShader("assets/shaders/default.glsl"));
                currentScene.render();      //be sure to bind a shader before this call
            }

            this.framebuffer.unbind();  //end render to framebuffer
            imGuiLayer.update(dt, currentScene);

//            DebugDrawBatch.draw(); // for testing viewport dimensions

            endFrame();

        }

        currentScene.save();
    }

    protected void startFrame(){



    }

    protected void endFrame(){
        glfwSwapBuffers(glfwWindow);

        MouseListener.endFrame();

        endTime = (float) glfwGetTime();
        dt = endTime - beginTime;
        beginTime = endTime;
    }

    public long getGlfwWindow(){
        return glfwWindow;
    }

    public static int getWidth() {
        return get().config.getWidth();
    }

    public static int getHeight() {
        return get().config.getHeight();
    }

    public static float getAspectRatio() { return (float) getWidth() / (float) getHeight(); }
}
