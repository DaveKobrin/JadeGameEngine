package Jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private double dragStartX, dragStartY;
    private double dragStopX, dragStopY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;

        //if mouse button is pressed during a position change then dragging is true
        if (!get().isDragging && (get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2])) {
            get().isDragging = true;
            get().dragStartX = xpos;
            get().dragStartY = ypos;
        }
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                if (get().isDragging && !(get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2])) {
                    get().isDragging = false;
                    get().dragStopX = get().xPos;
                    get().dragStopY = get().yPos;
                }
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float)(get().xPos);
    }

    public static float getY() {
        return (float)(get().yPos);
    }

    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getDragStartX() {
        return (float)(get().dragStartX);
    }

    public static float getDragStartY() {
        return (float)(get().dragStartY);
    }

    public static float getDragStopX() {
        return (float)(get().dragStopX);
    }

    public static float getDragStopY() {
        return (float)(get().dragStopY);
    }

    public static float getDeltaDragX() {
        return (float)(get().dragStartX - get().dragStopX);
    }

    public static float getDeltaDragY() {
        return (float)(get().dragStartY - get().dragStopY);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}