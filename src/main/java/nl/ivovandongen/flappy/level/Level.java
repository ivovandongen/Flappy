package nl.ivovandongen.flappy.level;

import nl.ivovandongen.flappy.graphics.Debug;
import nl.ivovandongen.flappy.graphics.Program;
import nl.ivovandongen.flappy.graphics.Texture;
import nl.ivovandongen.flappy.graphics.VertexArray;
import nl.ivovandongen.flappy.input.Input;
import nl.ivovandongen.flappy.math.Matrix4f;
import nl.ivovandongen.flappy.math.Vector3f;

import java.io.IOException;
import java.util.Random;

public class Level {
    private static Random random = new Random();
    private static int PIPES_OFFSET = 5;

    private VertexArray background;
    private Texture texture;

    private VertexArray fade;

    private int xScroll = 0;
    private int map = 0;

    private Bird bird;
    private Pipe[] pipes = new Pipe[10];
    private int index;
    private boolean control = true;

    private float time;

    public Level(float aspectRatio) {
        Debug.checkError();
        background = new VertexArray(
                new float[]{
                        -10f, -10f * aspectRatio, 0,
                        -10f, 10f * aspectRatio, 0,
                        0, 10f * aspectRatio, 0,
                        0, -10f * aspectRatio, 0
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
            texture = Texture.create(getClass().getResourceAsStream("/textures/bg.jpeg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Debug.checkError();

        bird = new Bird();

        generatePipes();

        fade = new VertexArray(
                new float[]{
                        -1.0f, -1.0f, 0.5f,
                        -1.0f, 1.0f, 0.5f,
                        1.0f, 1.0f, 0.5f,
                        1.0f, -1.0f, 0.5f
                },
                new byte[]{
                        0, 1, 2,
                        2, 3, 0
                }
        );
    }

    public void update(Input input) {
        if (control) {
            xScroll--;
            if (-xScroll % 335 == 0) {
                map++;
            }

            if (-xScroll > 250 && -xScroll % 120 == 0) {
                updatePipes();
            }
        }

        if (control && collision()) {
            bird.fall();
            control = false;
        }

        bird.update(control ? input : null);

        if (control && bird.getY() < -11f || bird.getY() > 11f) {
            System.out.println("Out of bounds");
            control = false;
        }

        if (!control && time > 0) {
            time -= .01;
        }

        if (control && time < 1) {
            time += .03;
        }
    }

    public boolean isGameOver() {
        return !control;
    }

    public void render() {
        texture.bind();
        Program.BG.bind();
        Program.BG.setUniform2f("bird", 0, bird.getY());
        background.bind();

        for (int i = 0; i < map + 4; i++) {
            Program.BG.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll * 0.03f, 0, 0)));
            background.draw();
        }

        bird.render();

        renderPipes();


        Program.FADE.bind();
        Program.FADE.setUniform1f("time", time);
        fade.render();
    }

    private boolean collision() {
        for (int i = 0; i < pipes.length; i++) {
            float bx = -xScroll * 0.05f;
            float by = bird.getY();
            float px = pipes[i].getX();
            float py = pipes[i].getY();

            float bx0 = bx - bird.getSize() / 2.0f;
            float bx1 = bx + bird.getSize() / 2.0f;
            float by0 = by - bird.getSize() / 2.0f;
            float by1 = by + bird.getSize() / 2.0f;

            float px0 = px;
            float px1 = px + Pipe.getWidth();
            float py0 = py;
            float py1 = py + Pipe.getHeight();

            if (bx1 > px0 && bx0 < px1) {
                if (by1 > py0 && by0 < py1) {
                    return true;
                }
            }

        }
        return false;
    }

    private void generatePipes() {
        for (int i = 0; i < 5 * 2; i += 2) {
            pipes[i] = new Pipe(PIPES_OFFSET + index * 3.0f, random.nextFloat() * 4.0f);
            pipes[i + 1] = new Pipe(pipes[i].getX(), pipes[i].getY() - 11.5f);
            index += 2;
        }
    }

    private void updatePipes() {
        pipes[index % 10] = new Pipe(PIPES_OFFSET + index * 3.0f, random.nextFloat() * 4.0f);
        pipes[(index + 1) % 10] = new Pipe(pipes[index % 10].getX(), pipes[index % 10].getY() - 11.5f);
        index += 2;
    }

    private void renderPipes() {
        Program.PIPE.bind();
        Program.PIPE.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(xScroll * 0.05f, 0, 0)));
        Program.PIPE .setUniform2f("bird", 0, bird.getY());
        Pipe.bind();

        for (int i = 0; i < pipes.length; i++) {
            Program.PIPE.setUniformMat4f("ml_matrix", pipes[i].getModelMatrix());
            Program.PIPE.setUniform1i("top", i % 2 == 0 ? 1 : 0);
            pipes[i].draw();
        }
    }
}
