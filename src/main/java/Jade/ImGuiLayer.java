package Jade;


import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

public class ImGuiLayer {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private Window window;

    ImGuiLayer() {
        window = Window.get();

    }

    public void init(String glslVersion){
        ImGui.createContext();
        imGuiGlfw.init(window.getGlfwWindow(), true);
        imGuiGl3.init(glslVersion);
    }

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void update(float dt) {
        startFrame();
        preProcess();
        process();
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

//        GLFW.glfwSwapBuffers(window.getGlfwWindow());
    }

    protected void process() {
        ImGui.text("Hello, World! ImGui is working!!!");
    }
    protected void preProcess() {

    }

    protected void postProcess() {

    }
}
