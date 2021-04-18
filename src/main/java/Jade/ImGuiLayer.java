package Jade;


import Editor.GameViewWindow;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;

/**
 * ImGuiLayer - Abstraction layer for the java binding of Dear ImGui from SpaiR/imgui-java
 *              on github. This supports all the UI code.
 */
public class ImGuiLayer {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private Window window;
    private ImFont imFontTest;
    private ImFont imFontDefault;


    ImGuiLayer() {
        window = Window.get();
    }

    public void init(String glslVersion){
        // Must create context before any other ImGui calls
        ImGui.createContext();
        ImGui.getIO().setIniFilename("imGui.ini");
        ImGui.getIO().setConfigFlags(ImGuiConfigFlags.DockingEnable);
        ImFontAtlas imFontAtlas = ImGui.getIO().getFonts();

        imFontDefault = imFontAtlas.addFontDefault();
        imFontTest = imFontAtlas.addFontFromFileTTF("assets/fonts/Candara.ttf", 16.0f);
        imFontAtlas.build();




        imGuiGlfw.init(window.getGlfwWindow(), true);
        imGuiGl3.init(glslVersion);

        //imFontConfig.destroy();
    }

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void update(float dt, Scene currentScene) {
        startFrame();
        preProcess();
        process(dt, currentScene);
        postProcess();
        endFrame();
    }

    protected void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    protected void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

    }

    protected void process(float dt, Scene currentScene) {
        ImGui.pushFont(imFontTest);
        currentScene.objectImGui();
        currentScene.imGui();
        GameViewWindow.imgui();
        ImGui.popFont();
    }

//    protected void process() {
//        ImGui.pushFont(imFontDefault);
//        ImGui.text("Hello, World! ImGui is working!!!");
//        ImGui.popFont();
//        ImGui.pushFont(imFontTest);
//        ImGui.text("different font testing");
//        ImGui.popFont();
//    }
    protected void preProcess() {
        //setup docking
        setupDockspace();
    }

    protected void postProcess() {
        //end docking ImGui window
        ImGui.end();
    }

    private void setupDockspace() {
        //setup main ImGui root window
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        //setup parent window to dock into always full size of game window, always in back, not movable or resizeable
        //and not focusable
        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight(), ImGuiCond.Always);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f);


        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);
// -------------debug testing
//        ImVec2 availableSize = new ImVec2();
//        ImGui.getContentRegionAvail(availableSize);
//
//        ImVec2 currPos = new ImVec2();
//        ImGui.getCursorScreenPos(currPos);
//        System.out.println("dockspace window dim from (" + currPos.x + ", " + currPos.y + " ) to (" + availableSize.x +", " + availableSize.y + ")");
//----------------------------test end

        //dockspace
        ImGui.dockSpace(ImGui.getID("dockspace"));
    }
}
