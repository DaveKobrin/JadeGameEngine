package Renderer;

import Components.SpriteRenderer;
import Jade.Window;
import Utility.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * RenderBatch - maintains a collection of sprites to send to the GPU in a single upload
 *               this is for improved performance over single draw calls
 */
public class RenderBatch implements Comparable<RenderBatch> {
    /*===================
     *  VERTEX LAYOUT
     *===================
     * Pos                  Color                       Texture Info
     * Pos.x, Pos.y         red,   green, blue,  alpha  u,      v,      texID
     * float, float,        float, float, float, float, float,  float,  float
     */
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = POS_SIZE  + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final int MAX_TEXTURES = 16; // reserve texture slot 0 for null texture
    private final int[] TEXTURE_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private List<Texture> textures;

    private int maxBatchSize;

    private int vaoID;
    private int vboID;

    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        shader = AssetPool.getShader("assets/shaders/default.glsl");

        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;
        this.sprites = new SpriteRenderer[maxBatchSize];

        // 4 vertices per sprite
        this.vertices = new float[maxBatchSize * VERTEX_SIZE * 4];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
        this.textures.add(null);    //reserve texture0 for no texture
    }

    public void start() {
        //  Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //  Allocate GPU memory for vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES,GL_DYNAMIC_DRAW);

        //  Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //  Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT,false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT,false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

    }

    public void render() {

        boolean rebuffData = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.isChanged()) {
                loadVertexProperties(i);
                spr.resetChanged();
                rebuffData = true;
            }
        }

        if (rebuffData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        //use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        //reserve tex slot0 for no texture
        for (int i = 1; i < this.textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            this.textures.get(i).bind(); //
        }
        this.shader.uploadIntArray("uTextures", TEXTURE_SLOTS);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,this.numSprites * 6,GL_UNSIGNED_INT,0);

        //unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 1; i < this.textures.size(); i++) {
            this.textures.get(i).unbind();
        }

        shader.detach();
    }

    /**
     * addSprite(sprite) - adds SpriteRenderer to this batch.
     *                   !!! caller must ensure sprite can be added to this batch !!!
     *                   !!! will assert false if there is not room !!!
     * @param sprite - SpriteRenderer to add to the batch.
     */
    public void addSprite(SpriteRenderer sprite) {
        // get index and add renderObject
        if (this.hasRoom && hasTexRoom(sprite.getTexture())) {
            int index = this.numSprites;
            this.sprites[index] = sprite;

            if (sprite.getTexture() != null && !textures.contains(sprite.getTexture())) {
                this.textures.add(sprite.getTexture());
            }

            // add properties to local vertex array
            loadVertexProperties(index);

            if ((++this.numSprites) >= this.maxBatchSize) {
                this.hasRoom = false;
            }
        } else {
            assert false : "Not enough space in the sprite batch or texture batch";
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // find offset within array 4 vertices per sprite
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        int texID = 0;
        if (sprite.getTexture() != null) {
            for (texID = 1; texID < this.textures.size() ; texID++) {
                if (this.textures.get(texID).equals(sprite.getTexture())) {
                    break;
                }
            }
            if (texID >= this.textures.size()) {
                assert false : "ERROR! texture not found in batch";
            }

        }
        Vector2f[] texCoords = sprite.getTexCoords();


        // add vertices with correct properties
        float x = 1.0f;
        float y = 1.0f;

        for (int i = 0; i < 4; i++) {
            // *     *   add top right, bottom right
            // *     *   bottom left, and top left

            switch (i) {
                case 0:
                    x = 1.0f;
                    y = 1.0f;
                    break;
                case 1:
                    x = 1.0f;
                    y = 0.0f;
                    break;
                case 2:
                    x = 0.0f;
                    y = 0.0f;
                    break;
                case 3:
                    x = 0.0f;
                    y = 1.0f;
                    break;
            }

            // load data
            // position x, y
            vertices[0 + offset] = sprite.gameObject.getTransform().getPosition().x + (x * sprite.gameObject.getTransform().getScale().x);
            vertices[1 + offset] = sprite.gameObject.getTransform().getPosition().y + (y * sprite.gameObject.getTransform().getScale().y);

            // color r, g, b, a
            vertices[2 + offset] = color.x;
            vertices[3 + offset] = color.y;
            vertices[4 + offset] = color.z;
            vertices[5 + offset] = color.w;

            //tex coords u, v
            vertices[6 + offset] = texCoords[i].x;
            vertices[7 + offset] = texCoords[i].y;

            // texID
            vertices[8 + offset] = (float) texID;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        //  create array of indices to vertices for the quads in the batch
        //  each quad is 2 triangles with vertices in CCW order
        //  6 indices per quad (ie.  [3, 2, 0,   0, 2, 1] )
        int[] elements = new int[maxBatchSize * 6];

        //  loop to generate indices for each quad
        for (int i = 0; i < maxBatchSize; ++i) {
            genQuadIndices(elements,i);
        }
        return elements;
    }

    private void genQuadIndices(int[] elements, int index) {
        // insert the indices for quad[index] into the elements array
        // pattern is [3,2,0,0,2,1], [7,6,4,4,6,5],...
        int offsetArrayIndex = 6 * index;
        int offsetBetweenTriangles = 4 * index;

        // triangle 1
        elements[0 + offsetArrayIndex] = 3 + offsetBetweenTriangles;
        elements[1 + offsetArrayIndex] = 2 + offsetBetweenTriangles;
        elements[2 + offsetArrayIndex] = 0 + offsetBetweenTriangles;

        // triangle 2
        elements[3 + offsetArrayIndex] = 0 + offsetBetweenTriangles;
        elements[4 + offsetArrayIndex] = 2 + offsetBetweenTriangles;
        elements[5 + offsetArrayIndex] = 1 + offsetBetweenTriangles;

    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTexRoom(Texture tex) {
        if (tex == null) { return true; }
        if (this.textures.size() < MAX_TEXTURES || this.textures.contains(tex)) {
            return true;
        } else {
            return false;
        }
    }

    public int getzIndex() {
        return this.zIndex;
    }

    /**
     * compareTo(o) - comparable based on zIndex
     * @param o - another RenderBatch to compare with.
     * @return - 0 if this.zIndex == o.zIndex
     *           - if this.zIndex < o.zIndex
     *           + if this.zIndex > o.zIndex
     */
    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
