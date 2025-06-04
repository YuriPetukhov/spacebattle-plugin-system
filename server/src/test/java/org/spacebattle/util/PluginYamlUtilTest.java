package org.spacebattle.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.spacebattle.plugins.PluginComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PluginYamlUtilTest {

    @Test
    public void testLoadYamlFromJar() throws Exception {
        // создаём временный jar с plugin.yaml
        File tempJar = File.createTempFile("test-plugin", ".jar");
        tempJar.deleteOnExit();

        String yaml = """
            plugin:
              name: test-plugin
              components:
                - class: org.spacebattle.util.MockComponent
            """;

        try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(tempJar))) {
            jarOut.putNextEntry(new JarEntry("plugin.yaml"));
            jarOut.write(yaml.getBytes(StandardCharsets.UTF_8));
            jarOut.closeEntry();
        }

        Map<String, Object> pluginData = PluginYamlUtil.loadYamlFromJar(tempJar);
        assertNotNull(pluginData.get("plugin"));
        Map<String, Object> plugin = (Map<String, Object>) pluginData.get("plugin");
        assertEquals("test-plugin", plugin.get("name"));
    }

    @Test
    public void testApplyDynamicComponent() throws Exception {
        Map<String, Object> pluginSpec = Map.of(
                "components", java.util.List.of(
                        Map.of("class", PluginYamlUtilTest.MockComponent.class.getName())
                )
        );

        ClassLoader loader = getClass().getClassLoader();

        PluginYamlUtil.applyDynamicComponents(loader, pluginSpec);

        assertTrue(MockComponent.wasApplied);
    }


    // фиктивный компонент, имитирующий загрузку
    public static class MockComponent implements PluginComponent {
        public static boolean wasApplied = false;

        @Override
        public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
            wasApplied = true;
            System.out.println("MockComponent applied");
        }
    }
}
