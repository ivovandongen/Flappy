package nl.ivovandongen.flappy.level;

import nl.ivovandongen.flappy.graphics.Debug;
import nl.ivovandongen.flappy.graphics.Texture;
import nl.ivovandongen.flappy.graphics.VertexArray;
import nl.ivovandongen.flappy.math.Matrix4f;
import nl.ivovandongen.flappy.math.Vector3f;

import java.io.IOException;

public class Pipe {

    private static float width = 1.5f, height = 8f;
    private static Texture texture;
    private static VertexArray mesh;

    public static void init() {
        Debug.checkError();
        mesh = new VertexArray(
                new float[]{
                        .0f, .0f, .1f,
                        .0f, height, .1f,
                        width, height, .1f,
                        width, .0f, .1f
                },
                new byte[]{
                        0, 1, 2,
                        2, 3, 0
                },
                new float[]{
                        0, 1,
                        0, 0,
                        1, 0,
                        1, 1
                }
        );
        Debug.checkError();
        try {
            texture = Texture.create(Pipe.class.getResourceAsStream("/textures/pipe.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Debug.checkError();
    }

    private Vector3f position = new Vector3f();
    private Matrix4f mlMatrix;

    public Pipe(float x, float y) {
        position.x = x;
        position.y = y;
        mlMatrix = Matrix4f.translate(position);
    }

    public static void bind() {
        texture.bind();
        mesh.bind();
    }

    public void draw() {
        mesh.draw();
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public static float getHeight() {
        return height;
    }

    public static float getWidth() {
        return width;
    }

    public Matrix4f getModelMatrix() {
        return mlMatrix;
    }
}
