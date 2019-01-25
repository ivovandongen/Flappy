package nl.ivovandongen.flappy.graphics;

public interface GLResource {

    int getId();

    void bind();

    void unbind();

//    TODO: void cleanup();
}
