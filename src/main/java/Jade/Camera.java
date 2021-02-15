package Jade;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    private Vector2f position;
    private static final float WORLD_POS_LEFT = 0.0f;
    private static final float WORLD_POS_RIGHT = 32.0f * 40.0f;
    private static final float WORLD_POS_TOP = 32.0f * 21.0f;
    private static final float WORLD_POS_BOTTOM = 0.0f;
    private static final float NEAR_CLIP = 0.0f;
    private static final float FAR_CLIP = 100.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void setPosition(Vector2f newPos) {
        this.position = newPos;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public void adjustProjection() {
        //this.projectionMatrix.identity();
        this.projectionMatrix.setOrtho(WORLD_POS_LEFT, WORLD_POS_RIGHT, WORLD_POS_BOTTOM, WORLD_POS_TOP, NEAR_CLIP, FAR_CLIP);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        //this.viewMatrix.identity();
        this.viewMatrix.setLookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

}
