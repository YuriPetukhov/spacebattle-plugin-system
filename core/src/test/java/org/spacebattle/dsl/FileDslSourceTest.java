package org.spacebattle.dsl;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileDslSourceTest {

    @Test
    void testOpenStreamReturnsCorrectContent() throws Exception {
        // Arrange: создаём временный файл
        File tempFile = File.createTempFile("test-dsl", ".yml");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            writer.write("objects:\n  obj-1:\n    properties:\n      test: 123\n");
        }

        FileDslSource source = new FileDslSource(tempFile);

        String result;
        try (InputStream in = source.openStream()) {
            result = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertTrue(result.contains("test: 123"));

        assertTrue(tempFile.delete());
    }
}
