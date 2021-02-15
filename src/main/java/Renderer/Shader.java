package Renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {
    private int shaderProgramID;
    private String vertexSrc;
    private String fragmentSrc;
    private String filepath;
    private boolean beingUsed = false;

    public Shader(String filepath) {
        this.filepath = filepath;
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error could not open file for shader : '" + filepath +"'";
        }
        String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
        if (splitString.length < 2) {
            assert false : "Error shader '" + filepath + "' is not a valid shader";
        }
        String[] shadertype = new String[splitString.length-1];
        int count = 1;
        int startPos = 0;
        int endPos = 0;
        while (count < splitString.length) {
            startPos = source.indexOf("#type", endPos) + 6;
            endPos = source.indexOf("\r\n", startPos);
            shadertype[count-1] = source.substring(startPos, endPos).trim();

            switch (shadertype[count-1]) {
                case "vertex":
                    vertexSrc = splitString[count];
                    //System.out.println("vertex source = \n" + vertexSrc);
                    break;
                case "fragment":
                    fragmentSrc = splitString[count];
                    //System.out.println("fragment source = \n" + fragmentSrc);
                    break;
                default:
                    assert false : "Error shader '" + filepath + "' has invalid types";
            }
            ++count;
        }
    }

    public void compile() {
        //===========================
        //  Compile and link shaders
        //===========================

        //  first load and compile the vertex shader
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID,vertexSrc);
        glCompileShader(vertexID);

        //check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tvertex shader compile failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //  second load and compile the fragment shader
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        //check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tfragment shader compile failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //link vertex and fragment shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //check for link errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath +"'\n\tlinking of shader failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

    }

    public void use() {
        if (!beingUsed) {
            //bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation,false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation,false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] val) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1iv(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        use();
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1i(varLocation, slot);
    }
}
