package Utility;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Object for color data.
 */
@Data
@NoArgsConstructor
public class Color implements Cloneable {
    public enum COLORS {
        RED         (1.0f,0.0f,0.0f,1.0f),
        GREEN       (0.0f,1.0f,0.0f,1.0f),
        CYAN        (0.0f,1.0f,1.0f,1.0f),
        BLUE        (0.0f,0.0f,1.0f,1.0f),
        PURPLE      (0.5f,0.0f,1.0f,1.0f),
        MAGENTA     (1.0f,0.0f,1.0f,1.0f),
        YELLOW      (1.0f,1.0f,0.0f,1.0f),
        ORANGE      (1.0f,0.5f,0.0f,1.0f),
        DARK_GRAY   (0.2f,0.2f,0.2f,1.0f),
        MID_GRAY    (0.4f,0.4f,0.4f,1.0f),
        LIGHT_GRAY  (0.7f,0.7f,0.7f,1.0f),
        WHITE       (1.0f,1.0f,1.0f,1.0f),
        TRANSPARENT (0.0f,0.0f,0.0f,0.0f);


        float r, g, b, a;

        COLORS(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public float[] getColors() {
            return new float[]{this.r, this.g, this.b, this.a};
        }

        public Color getAsColor() { return new Color(getColors()); }
    }


    private final float[] data = new float[4];

    public Color(COLORS color) { set(color.getColors()); }

    public Color(final float[] color) {
        set(color);
    }

    public Color(final float red, final float green, final float blue, final float alpha) {
        set(red, green, blue, alpha);
    }

    public final void set(final float red, final float green, final float blue, final float alpha) {
        data[0] = red;
        data[1] = green;
        data[2] = blue;
        data[3] = alpha;
    }

    public final void set(final float[] color) {
        System.arraycopy(color, 0, data, 0, 4);
    }

    public final float getRed() {
        return data[0];
    }

    public final float getGreen() {
        return data[1];
    }

    public final float getBlue() {
        return data[2];
    }

    public final float getAlpha() {
        return data[3];
    }

    /**
     * Method to clone Color instance.
     *
     * @return cloned Color instance
     */
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Color clone() {
        return new Color(data);
    }
}
