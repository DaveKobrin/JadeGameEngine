package Editor;

import Jade.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GameViewWindow {

    @Getter
    private  ImVec2 viewportSize = new ImVec2();
    private  ImVec2 viewportLocalPos = new ImVec2();
    @Getter
    private  ImVec2 viewportScreenPos = new ImVec2();
//    @Getter
//    private static float viewportScaleX = 0f;
//    @Getter
//    private static float viewportScaleY = 0f;

    //debug only
    private  ImVec2 debugMaxAvailSize = new ImVec2();

    public  void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        viewportSize = getMaxViewportSize();
        viewportLocalPos = getCenteredViewportPos(viewportSize);
        setViewportScreenPos(viewportLocalPos);

        ImGui.setCursorPos(viewportLocalPos.x, viewportLocalPos.y);
        int textureID = Window.get().getFramebuffer().getTexID();
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0,1,1,0);

        ImGui.end();
    }

    private  void setViewportScreenPos(ImVec2 localPos) {
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX() + 10;
        topLeft.y -= ImGui.getScrollY() + 40;

        viewportScreenPos = new ImVec2(topLeft.x + localPos.x, topLeft.y + localPos.y);

    }

    private  ImVec2 getMaxViewportSize() {
        //get ImGui available size
        ImVec2 availableSize = new ImVec2();
        ImGui.getContentRegionAvail(availableSize);
        availableSize.x -= ImGui.getScrollX();
        availableSize.y -= ImGui.getScrollY();

        debugMaxAvailSize = availableSize.clone();

        //assume full width for viewport
        float aspectWidth = availableSize.x;
        float aspectHeight = aspectWidth / Window.getAspectRatio();
        //if aspectHeight > availableSize.y must shrink aspectWidth
        if (aspectHeight > availableSize.y) {
            aspectHeight = availableSize.y;
            aspectWidth = aspectHeight * Window.getAspectRatio();
        }

//        viewportScaleX = aspectWidth / Window.getScene().getCamera().getWorldSizeX();
//        viewportScaleY = aspectHeight / Window.getScene().getCamera().getWorldSizeY();

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private  ImVec2 getCenteredViewportPos(ImVec2 viewportSize) {
        //get ImGui available size
        ImVec2 availableSize = new ImVec2();
        ImGui.getContentRegionAvail(availableSize);
        availableSize.x -= ImGui.getScrollX() - 16;
        availableSize.y -= ImGui.getScrollY() - 60;

        ImVec2 viewPos = new ImVec2((availableSize.x / 2f) - (viewportSize.x / 2f), (availableSize.y / 2f) - (viewportSize.y / 2f));


        return viewPos;
    }

    public  float getViewportPosX() { return viewportScreenPos.x;}
    public  float getViewportPosY() { return viewportScreenPos.y;}
    public  float getViewportSizeX() { return viewportSize.x;}
    public  float getViewportSizeY() { return viewportSize.y;}

    public  boolean isInViewport(float posX, float posY) {

        ImVec2 posInScreenCoords = getVecInScreenCoords(new ImVec2(posX, posY));
        ImVec2 max = new ImVec2(viewportScreenPos.x + viewportSize.x, viewportScreenPos.y + viewportSize.y);

        return posInScreenCoords.x >= viewportScreenPos.x && posInScreenCoords.x < max.x &&
                posInScreenCoords.y >= viewportScreenPos.y && posInScreenCoords.y < max.y;
    }

    private  ImVec2 getVecInScreenCoords( final ImVec2 inVec) {

        Vector4f normPos = new Vector4f( inVec.x, inVec.y, 0, 1);
        normPos.mul(Window.getScene().getCamera().getWorld2NormalizedScreenMat()); //in GL NDC coords range (-1, 1)

        float X = (((normPos.x + 1f) / 2f) * viewportSize.x) + viewportScreenPos.x;
        float Y = ((((normPos.y + 1f) / 2f) * viewportSize.y) ) + viewportScreenPos.y;

        return new ImVec2(X, Y);
    }
}
