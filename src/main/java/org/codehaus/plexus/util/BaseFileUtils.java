package org.codehaus.plexus.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * Implementation specific to Java SE 8 version.
 */
abstract class BaseFileUtils {
    static String fileRead(Path path, String encoding) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return encoding != null ? new String(bytes, encoding) : new String(bytes);
    }

    static void fileWrite(Path path, String encoding, String data, OpenOption... openOptions) throws IOException {
        byte[] bytes = encoding != null ? data.getBytes(encoding) : data.getBytes();
        Files.write(path, bytes, openOptions);
    }
}
