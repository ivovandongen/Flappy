package nl.ivovandongen.flappy.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static nl.ivovandongen.flappy.util.BufferUtils.*;
import static org.lwjgl.opengl.GL11.*;

public class Texture implements GLResource {
    private final int height;
    private final int width;
    private final int id;

    private Texture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static Texture create(InputStream inputStream) throws IOException {
        int[] pixels;
        int id, width, height;

        BufferedImage image = ImageIO.read(inputStream);
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            // ARGB -> RGBA (Shift A 3 bytes to the right and move the rest over 1 byte to the left
            pixels[i] = (pixels[i] & 0xff000000) |
                            (pixels[i] & 0x00ff0000) >> 16 |
                            (pixels[i] & 0x0000ff00) |
                            (pixels[i] & 0x000000ff) << 16;
        }

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer(pixels));
        glBindTexture(GL_TEXTURE_2D, 0);

        return new Texture(id, width, height);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
