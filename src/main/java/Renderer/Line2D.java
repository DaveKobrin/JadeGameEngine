package Renderer;

import Utility.Color;
import org.joml.Vector2f;

/**
 *  Line2D - information to draw a line
 */
public class Line2D {
    private Vector2f start;     // start of line (x,y)
    private Vector2f end;       // end of line (x,y)
    private Color color;        // color to draw line (rgba)
    private int lifetime;       // how many frames should the line persist

    public Line2D(Vector2f start, Vector2f end, Color color, int lifetime) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.lifetime = lifetime;
    }

    public Line2D(Vector2f start, Vector2f end) {
        this(start, end, Color.COLORS.DARK_GRAY.getAsColor(), 1);
    }

    public int beginFrame() {
        //used to keep track of frames of lifetime
        return --this.lifetime;
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }

    public int getLifetime() {
        return lifetime;
    }

    public float getLength() {
        return new Vector2f(this.end).sub(this.start).length();
    }

    public float getLengthSquared(){
        return new Vector2f(this.end).sub(this.start).lengthSquared();
    }
}
