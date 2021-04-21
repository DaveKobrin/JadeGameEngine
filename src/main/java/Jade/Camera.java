package Jade;


import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Camera - manage the location and direction of the camera looking into the
 *          game world.
 */
public class Camera {

    private static final float WORLD_POS_LEFT = 0.0f;
    private static final float WORLD_POS_RIGHT = 32.0f * 40.0f;
    private static final float WORLD_POS_TOP = 32.0f * 21.0f;
    private static final float WORLD_POS_BOTTOM = 0.0f;
    private static final float NEAR_CLIP = 0.0f;
    private static final float FAR_CLIP = 100.0f;

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    @Getter
    private Matrix4f invProjMatrix;
    @Getter
    private Matrix4f invViewMatrix;
    @Getter
    @Setter
    private Vector2f position;
    @Getter
    private Vector2f projectionSize = new Vector2f(WORLD_POS_RIGHT, WORLD_POS_TOP);


    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.invProjMatrix = new Matrix4f();
        this.invViewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.setOrtho(WORLD_POS_LEFT, WORLD_POS_RIGHT, WORLD_POS_BOTTOM, WORLD_POS_TOP, NEAR_CLIP, FAR_CLIP);
        this.projectionMatrix.invert(invProjMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.setLookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        this.viewMatrix.invert(invViewMatrix);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getWorld2NormalizedScreenMat() {
        Matrix4f result = new Matrix4f();
        projectionMatrix.mul(viewMatrix, result);
        return result;
    }

    public Matrix4f getNormalizedScreen2WorldMat() {
        Matrix4f result = new Matrix4f();
        invViewMatrix.mul(invProjMatrix, result);
        return result;
    }

    public float getWorldSizeX() { return WORLD_POS_RIGHT - WORLD_POS_LEFT; }
    public float getWorldSizeY() { return WORLD_POS_TOP - WORLD_POS_BOTTOM; }

}
