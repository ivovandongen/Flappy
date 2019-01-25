package nl.ivovandongen.flappy;

import nl.ivovandongen.flappy.graphics.Program;
import nl.ivovandongen.flappy.input.Input;
import nl.ivovandongen.flappy.level.Level;
import nl.ivovandongen.flappy.level.Pipe;
import nl.ivovandongen.flappy.math.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static nl.ivovandongen.flappy.graphics.Debug.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Main {

    private GLFWErrorCallback errorCallback;
    private float ratio = 9f / 16f;
    private int width = 1280;
    private int height = (int) (width * ratio);
    private long window;
    private Input input = new Input();
    private Level level;

    private void start() throws IOException {
        boolean running = true;
        init();

        checkError();
        level = new Level(ratio);
        checkError();

        long lastTime = System.nanoTime();
        double delta = 0;
        double ns = 1000000000.0;
        double sixty_hrz = ns / 60;

        double timer = System.currentTimeMillis();

        int updates = 0;
        int frames = 0;

        while (running) {
            glfwPollEvents();

            long now = System.nanoTime();
            delta += (now - lastTime) / sixty_hrz;
            lastTime = now;
            if (delta >= 1) {
                update();
                updates++;
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
                System.out.println(String.format("UPS: %s, FPS: %s", updates, frames));

                frames = 0;
                updates = 0;
            }

            if (glfwWindowShouldClose(window)) {
                running = false;
            }
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }


    public static void main(String[] args) throws IOException {
        // Make sure awt doesn't try to start on this thread (glfw already uses it).
        // Needed for ImageIO for example
        System.setProperty("java.awt.headless", "true");

        System.out.println("Welcome. Running from: " + System.getProperty("user.dir"));

        new Main().start();
    }

    private void init() throws IOException {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit()) {
            System.err.println("Could not initialize GLFW");
            return;
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(width, height, "Flappy", 0, 0);

        if (window == 0) {
            System.err.println("Could not create window");
            return;
        }

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        // Hook up lwjgl with the glfw context
        GL.createCapabilities();

        // TODO: Enable vsync
        glfwSwapInterval(1);

        // Set input handler
        glfwSetKeyCallback(window, input);

        String version = glGetString(GL_VERSION);
        System.out.println("Using OpenGL version: " + version);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.3f, 0, 0, 1);

        // PR matrix
        Matrix4f prMatrix = Matrix4f.orthographic(-10f, 10f, -10f * ratio, 10f * ratio, -1, 1);

        // Initialize programs
        checkError();
        Program.loadAll();
        checkError();
        Program.BG.bind();
        checkError();
        Program.BG.setUniformMat4f("pr_matrix", prMatrix);
        checkError();
        glActiveTexture(GL_TEXTURE1);
        Program.BG.setUniform1i("tex", 1);
        checkError();

        Program.BIRD.bind();
        checkError();
        Program.BIRD.setUniformMat4f("pr_matrix", prMatrix);
        checkError();
        glActiveTexture(GL_TEXTURE1);
        Program.BIRD.setUniform1i("tex", 1);
        checkError();

        Program.PIPE.bind();
        checkError();
        Program.PIPE.setUniformMat4f("pr_matrix", prMatrix);
        checkError();
        glActiveTexture(GL_TEXTURE1);
        Program.PIPE.setUniform1i("tex", 1);
        checkError();
        Pipe.init();

        Program.FADE.bind();
        checkError();
        Program.FADE.setUniformMat4f("pr_matrix", Matrix4f.orthographic(-1,1,-1,1, -1,1));
        checkError();
    }

    private void update() {
        level.update(input);

        if (level.isGameOver() && input.isPressed(GLFW_KEY_SPACE )) {
            System.out.println("Game restarting");
            level = new Level(ratio);
        }
    }

    private void render() {
        checkError();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        checkError();

        level.render();

        checkError();

        glfwSwapBuffers(window);
    }
}
