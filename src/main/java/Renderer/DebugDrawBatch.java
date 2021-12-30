package Renderer;

import Jade.Window;
import Utility.AssetPool;
import Utility.Color;
import Utility.JMath;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Batch render lines for debug use
 */
public class DebugDrawBatch {
    private static int MAX_LINES = 500;
    private static List<Line2D> lines = new ArrayList<>();

    // Vertex Layout
    //      position                    color
    // xPos,    yPos,   zPos        r,      g,      b
    // float,   float,  float       float,  float,  float

    private static final int POS_SIZE = 3;
    private static final int COLOR_SIZE = 3;

    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = POS_SIZE  + COLOR_SIZE;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static float[] vertexArray = new float[MAX_LINES * VERTEX_SIZE * 2];    // 2 vertexes per line

    private static int vaoID;
    private static int vboID;

    private static Shader shader = AssetPool.getShader("assets/shaders/debug_line2d.glsl");

    private static boolean started = false;

    public static void start() {
        //  Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //  Allocate GPU memory for vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES,GL_DYNAMIC_DRAW);

        //  Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT,false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        //set line width
        glLineWidth(2.0f);

    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        // remove dead lines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i--);      //remove current line and reposition iterator
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0)      //nothing to draw
            return;

        int offset = 0;
        for (Line2D line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f pos = i == 0 ? line.getStart() : line.getEnd();

                // load data
                // position x, y, z
                vertexArray[0 + offset] = pos.x;
                vertexArray[1 + offset] = pos.y;
                vertexArray[2 + offset] = -10.0f;       //default z

                // color r, g, b
                vertexArray[3 + offset] = line.getColor().getRed();
                vertexArray[4 + offset] = line.getColor().getGreen();
                vertexArray[5 + offset] = line.getColor().getBlue();

                offset += VERTEX_SIZE;
            }
        }

        //copy data to gpu
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * VERTEX_SIZE * 2));

        //use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES,0, lines.size() * VERTEX_SIZE * 2); //from vertex 0 to vertex lines.size.end

        //unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    //---------------------------------------------------------------------------------------------------
    //              Add Lines to batch
    //---------------------------------------------------------------------------------------------------
    public static void addLine2D(Vector2f start, Vector2f end, Color color, int lifetime) {
        if (lines.size() >= MAX_LINES)
            return;
        DebugDrawBatch.lines.add(new Line2D(start, end, color, lifetime));
    }

    public static void addLine2D(Vector2f start, Vector2f end, Color color) {
        DebugDrawBatch.addLine2D(start, end, color, 1);
    }

    public static void addLine2D(Vector2f start, Vector2f end, int lifetime) {
        DebugDrawBatch.addLine2D(start, end, Color.COLORS.GREEN.getAsColor(), lifetime);
    }

    public static void addLine2D(Vector2f start, Vector2f end) {
        DebugDrawBatch.addLine2D(start,end, Color.COLORS.GREEN.getAsColor(), 1);
    }

    //---------------------------------------------------------------------------------------------------
    //              Add Box2D to batch
    //---------------------------------------------------------------------------------------------------
    public static void addBoxCorners(final Vector2f corner1, final Vector2f corner2, final Color color, int lifetime) {
        Vector2f lowerLeft = new Vector2f(Math.min(corner1.x, corner2.x), Math.min(corner1.y, corner2.y));
        Vector2f upperRight = new Vector2f(Math.max(corner1.x, corner2.x), Math.max(corner1.y, corner2.y));
        Vector2f lowerRight = new Vector2f(upperRight.x, lowerLeft.y);
        Vector2f upperLeft = new Vector2f(lowerLeft.x, upperRight.y);

        Vector2f[] vertices  = { lowerLeft, lowerRight, upperRight, upperLeft };

        addLine2D(vertices[0], vertices[1], color, lifetime);
        addLine2D(vertices[1], vertices[2], color, lifetime);
        addLine2D(vertices[2], vertices[3], color, lifetime);
        addLine2D(vertices[3], vertices[0], color, lifetime);
    }

    public static void addBoxCorners(final Vector2f corner1, final Vector2f corner2, final Color color){
        addBoxCorners(corner1, corner2, color, 1);
    }

    public static void addBoxCorners(final Vector2f corner1, final Vector2f corner2) {
        addBoxCorners(corner1, corner2, Color.COLORS.RED.getAsColor(), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, Color color, int lifetime) {
        Vector2f lowerLeft = new Vector2f(center).sub(new Vector2f(size).div(2.0f));
        Vector2f upperRight = new Vector2f(center).add(new Vector2f(size).div(2.0f));
        Vector2f lowerRight = new Vector2f(upperRight.x, lowerLeft.y);
        Vector2f upperLeft = new Vector2f(lowerLeft.x, upperRight.y);

        Vector2f[] vertices  = { lowerLeft, lowerRight, upperRight, upperLeft };

        if (!JMath.compare(rotation, 0.0f)) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1], color, lifetime);
        addLine2D(vertices[1], vertices[2], color, lifetime);
        addLine2D(vertices[2], vertices[3], color, lifetime);
        addLine2D(vertices[3], vertices[0], color, lifetime);

    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, Color color) {
        DebugDrawBatch.addBox2D(center, size, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, int lifetime) {
        DebugDrawBatch.addBox2D(center, size, rotation, Color.COLORS.GREEN.getAsColor(), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation) {
        DebugDrawBatch.addBox2D(center, size, rotation, Color.COLORS.GREEN.getAsColor(), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, Color color) {
        DebugDrawBatch.addBox2D(center, size, 0f, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, int lifetime) {
        DebugDrawBatch.addBox2D(center, size, 0f, Color.COLORS.GREEN.getAsColor(), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size) {
        DebugDrawBatch.addBox2D(center, size, 0f, Color.COLORS.GREEN.getAsColor(), 1);
    }

    //---------------------------------------------------------------------------------------------------
    //              Add Circle to batch
    //---------------------------------------------------------------------------------------------------
    public static void addCircle(Vector2f center, float radius, Color color, int lifetime) {
        final int NUM_SEGMENTS = 20;
        final int INCREMENT = 360 / NUM_SEGMENTS;
        Vector2f[] vertices = new Vector2f[NUM_SEGMENTS];
        int currAngle = 0;

        for (int i = 0; i < NUM_SEGMENTS; i++) {
            Vector2f temp = new Vector2f(radius, 0f);
            JMath.rotate(temp, currAngle, new Vector2f(0f,0f));
            vertices[i] = new Vector2f(temp).add(center);

            if (i > 0)
                addLine2D(vertices[i-1], vertices[i], color, lifetime);

            currAngle += INCREMENT;
        }
        addLine2D(vertices[NUM_SEGMENTS - 1], vertices[0], color, lifetime);
    }

    public static void addCircle(Vector2f center, float radius, Color color) {
        DebugDrawBatch.addCircle(center, radius, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, int lifetime) {
        DebugDrawBatch.addCircle(center, radius, Color.COLORS.GREEN.getAsColor(), lifetime);
    }

    public static void addCircle(Vector2f center, float radius) {
        DebugDrawBatch.addCircle(center, radius, Color.COLORS.GREEN.getAsColor(), 1);
    }

}
