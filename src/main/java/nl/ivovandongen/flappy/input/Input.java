package nl.ivovandongen.flappy.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[Character.MAX_VALUE];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW.GLFW_RELEASE;
    }

    public boolean isPressed(int key) {
        return keys[key];
    }
}
