package org.spacebattle.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPluginScannerTest {

    private PluginScanner scanner;

    @BeforeEach
    void setUp() {
        scanner = new DefaultPluginScanner();
    }

    @Test
    void testFindJarsReturnsOnlyJarFiles() throws IOException {
        File tempDir = Files.createTempDirectory("plugins").toFile();
        File jar1 = new File(tempDir, "plugin-a.jar");
        File jar2 = new File(tempDir, "plugin-b.jar");
        File txt = new File(tempDir, "notes.txt");

        assertTrue(jar1.createNewFile());
        assertTrue(jar2.createNewFile());
        assertTrue(txt.createNewFile());

        List<File> jars = scanner.findJars(tempDir);
        assertEquals(2, jars.size());
        assertTrue(jars.stream().allMatch(f -> f.getName().endsWith(".jar")));

        jar1.delete();
        jar2.delete();
        txt.delete();
        tempDir.delete();
    }

    @Test
    void testFindJarsReturnsEmptyListIfNoneFound() throws IOException {
        File tempDir = Files.createTempDirectory("empty-plugins").toFile();
        List<File> jars = scanner.findJars(tempDir);
        assertTrue(jars.isEmpty());
        tempDir.delete();
    }

    @Test
    void testFindJarsReturnsEmptyListIfNull() {
        File fakeDir = new File("non-existing-dir");
        List<File> jars = scanner.findJars(fakeDir);
        assertTrue(jars.isEmpty());
    }
}
