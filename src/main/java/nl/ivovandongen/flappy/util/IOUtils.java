package nl.ivovandongen.flappy.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {
    private IOUtils() {

    }

    public static String toString(final InputStream file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
        }
        return result.toString();
    }

    public static String toString(final File file) throws IOException {
        try {
            return toString(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + file.getAbsolutePath());
            throw e;
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            throw e;
        }
    }
}
