package Editor;

import Jade.GameObject;
import Jade.MouseListener;
import Jade.Window;
import imgui.ImGui;
import lombok.Getter;
import lombok.Setter;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    @Getter
    @Setter
    protected GameObject activeGameObject = null;

    /**
     * imGui() - process UI items specific to the active GameObject
     */
    public void imGui() {
        //process GameObject specific GUI items
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imGui();
            ImGui.end();
        }
    }

    public void update(float dt, Scene currentScene){
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging()){
            if (Window.getScene().getGameViewWindow().isInViewport(MouseListener.getViewportOrthoX(), MouseListener.getViewportOrthoY())) {
                int x = (int) MouseListener.getViewportXPos();
                int y = (int) MouseListener.getViewportYPos();
                int entID = Window.get().getPickingTexture().readPixel(x, y);

                System.out.println("x,y : " + x + "," + y + "entityID : " + entID);

                GameObject go = currentScene.getGoByUID(entID);
                if (go != null) {
                    activeGameObject = go;
                } else {
                    activeGameObject = null;
                }
            }
        }
    }
}
