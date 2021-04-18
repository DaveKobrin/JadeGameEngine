package Renderer;

import lombok.Getter;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * Texture - an image sent to the GPU for use with the shader
 */
public class Texture {
    @Getter
    private String filepath;
    @Getter
    private transient int texID;
    @Getter
    private int width;
    @Getter
    private int height;

    public Texture() {
        //required default constructor to allow serialization, but will be unusable until after init
        //had to explicitly add this to allow for framebuffer Texture construction
        this.filepath = "INVALID";
        this.texID = -1;
        this.width = -1;
        this.height = -1;
    }

    public Texture(int width, int height) {
        //constructor for framebuffer
        this.filepath = "Generated";

        //generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        //when stretching, blur
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        //when shrinking, blur
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public void init( String filepath ) {
        this.filepath = filepath;

        //generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        //set texture parameters

        //repeat in u and v directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //when stretching, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //when shrinking, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

            switch (channels.get(0)) {
                case 3:     //RGB image no alpha channel
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB,GL_UNSIGNED_BYTE, image);
                    break;
                case 4:     //RGBA image with alpha channel
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA,GL_UNSIGNED_BYTE, image);
                    break;
                default:
                    assert false : "Error: (Texture) Unknown number of channels.";
            }
        } else {
            assert false : "Error: (texture) Could not load image '" + filepath +"'";
        }

        //free stbi memory
        stbi_image_free(image);
    }
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public boolean equals(Object obj) {
        // if obj is nothing or not a Texture return immediately
        if (obj == null)
            return false;
        if (!(obj instanceof Texture))
            return false;

        //cast to Texture and compare all data
        Texture tex = (Texture) obj;
        return (tex.getWidth() == this.getWidth() && tex.getHeight() == this.getHeight() &&
                tex.getTexID() == this.getTexID() && tex.getFilepath().equals(this.getFilepath()));
    }

}
