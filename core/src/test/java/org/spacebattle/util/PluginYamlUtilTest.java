package org.spacebattle.util;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PluginYamlUtilTest {

    private File createJarWithPluginYaml(String yamlContent) throws IOException {
        File tempJar = File.createTempFile("plugin", ".jar");

        try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(tempJar))) {
            JarEntry entry = new JarEntry("plugin.yaml");
            jarOut.putNextEntry(entry);
            jarOut.write(yamlContent.getBytes());
            jarOut.closeEntry();
        }

        tempJar.deleteOnExit();
        return tempJar;
    }

    @Test
    void testLoadYamlFromValidJar() throws Exception {
        String yaml = """
            plugin:
              name: test-plugin
              version: 1.0
              commands: []
            """;

        File jarFile = createJarWithPluginYaml(yaml);

        Map<String, Object> result = PluginYamlUtil.loadYamlFromJar(jarFile);

        assertNotNull(result);
        assertTrue(result.containsKey("plugin"));

        Map<String, Object> plugin = (Map<String, Object>) result.get("plugin");
        assertEquals("test-plugin", plugin.get("name"));
        assertEquals(1.0, plugin.get("version"));
    }

    @Test
    void testLoadYamlFromJar_missingYaml_shouldThrow() throws Exception {
        // Создаём JAR без plugin.yaml
        File jarFile = File.createTempFile("empty", ".jar");
        try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarFile))) {
            jarOut.putNextEntry(new JarEntry("somefile.txt"));
            jarOut.write("test".getBytes());
            jarOut.closeEntry();
        }

        jarFile.deleteOnExit();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PluginYamlUtil.loadYamlFromJar(jarFile)
        );

        assertTrue(ex.getMessage().contains("plugin.yaml not found"));
    }

    @Test
    void testLoadYamlFromJar_invalidYaml_shouldThrow() throws Exception {
        // Некорректный YAML (в runtime упадёт в SnakeYAML при парсинге)
        String yaml = ": : :";

        File jarFile = createJarWithPluginYaml(yaml);

        Exception ex = assertThrows(Exception.class, () -> PluginYamlUtil.loadYamlFromJar(jarFile));
        assertTrue(ex.getMessage().contains("while scanning a simple key") // SnakeYAML error
                || ex instanceof RuntimeException);
    }
}
