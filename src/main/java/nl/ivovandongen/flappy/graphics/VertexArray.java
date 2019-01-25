package nl.ivovandongen.flappy.graphics;

import static nl.ivovandongen.flappy.util.BufferUtils.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray implements GLResource {

    private final int count;
    private final int vao;
    private final int vbo;
    private final int tbo;
    private final int ibo;

    public VertexArray(float[] vertices, byte[] indices, float[] textureCoordinates) {
        count = indices.length;

        // Setup VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        Debug.checkError();

        // Setup VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        Debug.checkError();

        // Setup tbo
        tbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER, buffer(textureCoordinates), GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        Debug.checkError();

        // Setup ibo
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer(indices), GL_STATIC_DRAW);

        Debug.checkError();

        // Unbind all the stuff
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        Debug.checkError();
    }

    public VertexArray(float[] vertices, byte[] indices) {
        count = indices.length;

        // Setup VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        Debug.checkError();

        // Setup VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        Debug.checkError();

        // Setup ibo
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer(indices), GL_STATIC_DRAW);

        Debug.checkError();

        // No texture coordinates
        tbo = -1;

        // Unbind all the stuff
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        Debug.checkError();
    }

    @Override
    public int getId() {
        return vao;
    }

    @Override
    public void bind() {
        Debug.checkError();
        glBindVertexArray(vao);
        Debug.checkError();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        Debug.checkError();
    }

    @Override
    public void unbind() {
        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        Debug.checkError();
    }

    public void draw() {
        Debug.checkError();
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_BYTE, 0);
        Debug.checkError();
    }

    public void render() {
        bind();
        draw();
    }
}
