package nl.ivovandongen.flappy.graphics;

import static org.lwjgl.opengl.GL11.*;

public class Debug {

    private static boolean printErrors() {
        boolean error = false;
        int i;
        while ((i = glGetError()) != GL_NO_ERROR) {
            System.err.println("OpenGL error: " + i);
            error = true;
        }

        return !error;
    }

    public static void checkError() {
        assert (printErrors());
    }
}
