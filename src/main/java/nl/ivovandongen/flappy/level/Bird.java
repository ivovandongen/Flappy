package nl.ivovandongen.flappy.level;

import nl.ivovandongen.flappy.graphics.Debug;
import nl.ivovandongen.flappy.graphics.Program;
import nl.ivovandongen.flappy.graphics.Texture;
import nl.ivovandongen.flappy.graphics.VertexArray;
import nl.ivovandongen.flappy.input.Input;
import nl.ivovandongen.flappy.math.Matrix4f;
import nl.ivovandongen.flappy.math.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class Bird {
    private float size = 1;
    private VertexArray mesh;
    private Vector3f position = new Vector3f();
    private Texture texture;
    private float rotation;
    private float yDelta = 0;

    public Bird() {
        Debug.checkError();
        mesh = new VertexArray(
                new float[]{
                        -size / 2, -size / 2, .1f,
                        -size / 2, size / 2, .1f,
                        size / 2, size / 2, .1f,
                        size / 2, -size / 2, .1f
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
            texture = Texture.create(getClass().getResourceAsStream("/textures/bird.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Debug.checkError();
    }

    protected void fall() {
        yDelta = -.15f;
    }

    public void update(Input input) {
        position.y -= yDelta;
        if (input != null && input.isPressed(GLFW.GLFW_KEY_SPACE)) {
            yDelta = -.10f;
        } else {
            yDelta += .01f;
        }

        rotation = -yDelta * 90;
    }


    public void render() {
        Program.BIRD.bind();
        Program.BIRD.setUniformMat4f("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rotation)));
        texture.bind();
        mesh.render();
    }

    public float getY() {
        return position.y;
    }

    public float getSize() {
        return size;
    }
}
