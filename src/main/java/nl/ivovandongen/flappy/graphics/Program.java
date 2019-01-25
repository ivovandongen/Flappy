package nl.ivovandongen.flappy.graphics;

import nl.ivovandongen.flappy.math.Matrix4f;
import nl.ivovandongen.flappy.math.Vector3f;
import nl.ivovandongen.flappy.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Program implements GLResource {

    public static Program BG;
    public static Program BIRD;
    public static Program PIPE;
    public static Program FADE;

    private final int id;
    private Map<String, Integer> uniformLocations = new HashMap<>();

    private Program(int id) {
        this.id = id;
    }

    public static Program create(InputStream vertexShader, InputStream fragmentShader) throws IOException {
        return new Program(create(
                IOUtils.toString(vertexShader),
                IOUtils.toString(fragmentShader)
        ));
    }

    public static Program create(File vertexShader, File fragmentShader) throws IOException {
        return new Program(create(
                IOUtils.toString(vertexShader),
                IOUtils.toString(fragmentShader)
        ));
    }

    public static void loadAll() throws IOException {
        BG = create(
                Program.class.getResourceAsStream("/shaders/bg.vert.glsl"),
                Program.class.getResourceAsStream("/shaders/bg.frag.glsl")
        );
        BIRD = create(
                Program.class.getResourceAsStream("/shaders/bird.vert.glsl"),
                Program.class.getResourceAsStream("/shaders/bird.frag.glsl")
        );
        PIPE = create(
                Program.class.getResourceAsStream("/shaders/pipe.vert.glsl"),
                Program.class.getResourceAsStream("/shaders/pipe.frag.glsl")
        );
        FADE = create(
                Program.class.getResourceAsStream("/shaders/fade.vert.glsl"),
                Program.class.getResourceAsStream("/shaders/fade.frag.glsl")
        );
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void bind() {
        Debug.checkError();
        glUseProgram(id);
        Debug.checkError();
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    public void setUniform1i(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform1f(String name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform2f(String name, float x, float y) {
        glUniform2f(getUniformLocation(name), x, y);
    }

    public void setUniform3f(String name, Vector3f vec3) {
        glUniform3f(getUniformLocation(name), vec3.x, vec3.y, vec3.z);
    }

    public void setUniformMat4f(String name, Matrix4f mat4) {
        glUniformMatrix4fv(getUniformLocation(name), false, mat4.toFloatBuffer());
    }

    private int getUniformLocation(String name) {
        Integer loc = uniformLocations.get(name);
        if (loc == null) {
            loc = glGetUniformLocation(id, name);
            if (loc == -1) {
                System.err.println("Could not find location for uniform: " + name);
                return loc;
            }
            uniformLocations.put(name, loc);
        }
        return loc;
    }

    private static int create(String vert, String frag) {
        int program = glCreateProgram();
        loadShader(program, vert, GL_VERTEX_SHADER);
        loadShader(program, frag, GL_FRAGMENT_SHADER);

        glLinkProgram(program);
        glValidateProgram(program);

        Debug.checkError();

        return program;
    }

    private static void loadShader(int program, String shaderSource, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String message = "Could not compile " + (type == GL_VERTEX_SHADER ? "Vertex" : "Fragment") + " shader\n" + glGetShaderInfoLog(shaderId, 2048);
            throw new RuntimeException(message);
        }
        glAttachShader(program, shaderId);
    }
}
