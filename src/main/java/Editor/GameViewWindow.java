package Editor;

import Jade.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import lombok.Getter;

public class GameViewWindow {

    @Getter
    private static ImVec2 viewportSize = new ImVec2();
    private static ImVec2 viewportLocalPos = new ImVec2();
    @Getter
    private static ImVec2 viewportScreenPos = new ImVec2();

    public static void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        viewportSize = getMaxViewportSize();
        viewportLocalPos = getCenteredViewportPos(viewportSize);
        setViewportScreenPos(viewportLocalPos);

        ImGui.setCursorPos(viewportLocalPos.x, viewportLocalPos.y);
        int textureID = Window.get().getFramebuffer().getTexID();
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0,1,1,0);

        ImGui.end();
    }

    private static void setViewportScreenPos(ImVec2 localPos) {
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        viewportScreenPos = new ImVec2(topLeft.x + localPos.x, topLeft.y + localPos.y);

// -------------debug testing
        ImVec2 availableSize = new ImVec2();
        ImGui.getContentRegionAvail(availableSize);

        ImVec2 currPos = new ImVec2();
        ImGui.getCursorScreenPos(currPos);
        System.out.println("viewport window dim from (" + currPos.x + ", " + currPos.y + " ) to (" + availableSize.x +", " + availableSize.y + ")");
//----------------------------test end
    }

    private static ImVec2 getMaxViewportSize() {
        //get ImGui available size
        ImVec2 availableSize = new ImVec2();
        ImGui.getContentRegionAvail(availableSize);
        availableSize.x -= ImGui.getScrollX();
        availableSize.y -= ImGui.getScrollY();

        //assume full width for viewport
        float aspectWidth = availableSize.x;
        float aspectHeight = aspectWidth / Window.getAspectRatio();
        //if aspectHeight > availableSize.y must shrink aspectWidth
        if (aspectHeight > availableSize.y) {
            aspectHeight = availableSize.y;
            aspectWidth = aspectHeight * Window.getAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static ImVec2 getCenteredViewportPos(ImVec2 viewportSize) {
        //get ImGui available size
        ImVec2 availableSize = new ImVec2();
        ImGui.getContentRegionAvail(availableSize);
        availableSize.x -= ImGui.getScrollX();
        availableSize.y -= ImGui.getScrollY();

        ImVec2 viewPos = new ImVec2((availableSize.x / 2f) - (viewportSize.x / 2f), (availableSize.y / 2f) - (viewportSize.y / 2f));


        return viewPos;
    }

    public static float getViewportPosX() { return viewportScreenPos.x;}
    public static float getViewportPosY() { return viewportScreenPos.y;}
    public static float getViewportSizeX() { return viewportSize.x;}
    public static float getViewportSizeY() { return viewportSize.y;}

    public static boolean isInViewport(float posX, float posY) {
        return (posX >= viewportScreenPos.x && posX <= viewportScreenPos.x + viewportSize.x) &&
                (posY >= viewportScreenPos.y && posY <= viewportScreenPos.y + viewportSize.y);
    }
}
