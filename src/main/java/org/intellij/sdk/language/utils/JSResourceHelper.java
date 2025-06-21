// File: org/intellij/sdk/language/util/JSResourceHelper.java

package org.intellij.sdk.language.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class JSResourceHelper {

    public static String extractScriptToTemp(String resourcePath, String fileName) {
        try (InputStream inputStream = JSResourceHelper.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("❌ Resource not found: " + resourcePath);
            }

            Path tempFile = Files.createTempFile(fileName, ".js");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit(); // Clean up after use

            return tempFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to extract JS file from resources", e);
        }
    }
}
