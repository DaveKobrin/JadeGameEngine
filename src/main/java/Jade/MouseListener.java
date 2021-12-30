package Jade;


import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * MouseListener - handle mouse events
 */
public class MouseListener {
    private static double scrollX = 0.0;
    private static double scrollY = 0.0;
    private static double xPos = 0.0;
    private static double yPos = 0.0;
    private static double lastX = 0.0;
    private static double lastY = 0.0;
    private static double worldXPos = 0.0;
    private static double worldYPos = 0.0;
    private static double worldVPXPos = 0.0;
    private static double worldVPYPos = 0.0;
    private static double worldVPLastX = 0.0;
    private static double worldVPLastY = 0.0;
    private static double dragStartX = 0.0;
    private static double dragStartY = 0.0;
    private static double dragStopX = 0.0;
    private static double dragStopY = 0.0;
    private static boolean mouseButtonPressed[] = new boolean[3];
    private static boolean isDragging = false;
    private static int numMouseButtonsDown = 0;


    public static void mousePosCallback(long window, double xpos, double ypos) {
        lastX = xPos;
        lastY = yPos;
        worldVPLastX = worldVPXPos;
        worldVPLastY = worldVPYPos;
        xPos = xpos;
        yPos = ypos;
        calcWorldPos();
        calcWorldVPPos();

        //if mouse button is pressed during a position change then dragging is true
        if (!isDragging && (numMouseButtonsDown > 0)) {
           isDragging = true;
           dragStartX = xpos;
           dragStartY = ypos;
        }

//        System.out.println("mouse pos worldVPPos ( " + worldVPXPos + ", " + worldVPYPos + " )");
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            ++numMouseButtonsDown;
            if (button < mouseButtonPressed.length) {
                mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            --numMouseButtonsDown;
            if (button < mouseButtonPressed.length) {
                mouseButtonPressed[button] = false;
                if (isDragging && (numMouseButtonsDown == 0)) {
                    isDragging = false;
                    dragStopX = xPos;
                    dragStopY = yPos;
                }
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        scrollX = xOffset;
        scrollY = yOffset;
    }

    public static void endFrame() {
        scrollX = 0;
        scrollY = 0;
        lastX = xPos;
        lastY = yPos;
        worldVPLastX = worldVPXPos;
        worldVPLastY = worldVPYPos;
    }

    public static float getViewportXPos() {
        float currX = getX() - Window.getScene().getGameViewWindow().getViewportPosX();
        float viewportXPos = (currX/Window.getScene().getGameViewWindow().getViewportSizeX()) * Window.getWidth(); //mouse xpos in viewport converted to full framebuffer size
        return viewportXPos;
    }

    public static float getViewportYPos() {
        float currY = getY() - Window.getScene().getGameViewWindow().getViewportPosY();
        float viewportYPos = Window.getHeight() - ((currY/Window.getScene().getGameViewWindow().getViewportSizeY()) * Window.getHeight()); //mouse ypos in viewport converted to full framebuffer size
        return viewportYPos;
    }


    private static void calcWorldPos(){
        float currX = getX();
        float currY = Window.getHeight() - getY();
        float currXNormalized = (currX/(float) Window.getWidth()) * 2.0f -1.0f; //GL Normalized Device Coordinate range (-1, 1)
        float currYNormalized = ((currY/(float) Window.getHeight()) * 2.0f -1.0f); //GL Normalized Device Coordinate range (-1, 1)
        worldXPos = getXinWorldCoords(currXNormalized);
        worldYPos = getYinWorldCoords(currYNormalized);
    }

    private static void calcWorldVPPos() {
        float currX = getX() - Window.getScene().getGameViewWindow().getViewportPosX();
        float currY = getY() - Window.getScene().getGameViewWindow().getViewportPosY();
        float currXNormalized = (currX/Window.getScene().getGameViewWindow().getViewportSizeX()) * 2.0f -1.0f; //GL Normalized Device Coordinate range (-1, 1)
        float currYNormalized = -((currY/Window.getScene().getGameViewWindow().getViewportSizeY()) * 2.0f -1.0f); //GL Normalized Device Coordinate range (-1, 1)
        worldVPXPos = getXinWorldCoords(currXNormalized);
        worldVPYPos = getYinWorldCoords(currYNormalized);
    }
    /**
     * getOrthoX() - get current mouse X pos in world coordinates
     * @return mouse X pos converted from screen coordinates to world coordinates
     */
    public static float getOrthoX() { return (float) worldXPos; }

    /**
     * getOrthoY() - get current mouse X pos in world coordinates
     * @return mouse X pos converted from screen coordinates to world coordinates
     */
    public static float getOrthoY() { return (float) worldYPos; }
    /**
     * getViewportOrthoX() - get current mouse X pos in world coordinates
     * @return mouse X pos converted from screen coordinates to world coordinates
     */
    public static float getViewportOrthoX() { return (float) worldVPXPos; }

    /**
     * getViewportOrthoY() - get current mouse X pos in world coordinates
     * @return mouse X pos converted from screen coordinates to world coordinates
     */
    public static float getViewportOrthoY() { return (float) worldVPYPos; }

    private static float getXinWorldCoords(float inX) {
        Vector4f currXVec4 = new Vector4f( inX, 0, 0, 1);
        currXVec4.mul(Window.getScene().getCamera().getNormalizedScreen2WorldMat());
        return currXVec4.x;
    }

    private static float getYinWorldCoords(float inY) {
        Vector4f currYVec4 = new Vector4f( 0, inY, 0, 1);
        currYVec4.mul(Window.getScene().getCamera().getNormalizedScreen2WorldMat());
        return currYVec4.y;
    }

    public static float getX() { return (float)xPos; }

    public static float getY() { return (float)yPos; }

    public static float getDx() {
        return (float) (xPos - lastX);
    }

    public static float getDy() {
        return (float) (yPos - lastY);
    }

    public static float getWorldVPDX() {
        return (float) (worldVPXPos - worldVPLastX);
    }

    public static float getWorldVPDY() {
        return (float) (worldVPYPos - worldVPLastY);
    }
    public static float getDragStartX() { return (float)dragStartX; }

    public static float getDragStartY() {
        return (float)dragStartY;
    }

    public static float getDragStopX() {
        return (float)dragStopX;
    }

    public static float getDragStopY() {
        return (float)dragStopY;
    }

    public static float getDeltaDragX() {
        return (float)(dragStartX - dragStopX);
    }

    public static float getDeltaDragY() {
        return (float)(dragStartY - dragStopY);
    }

    public static float getScrollX() {
        return (float)scrollX;
    }

    public static float getScrollY() {
        return (float)scrollY;
    }

    public static boolean isDragging() {
        return isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < mouseButtonPressed.length) {
            return mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}