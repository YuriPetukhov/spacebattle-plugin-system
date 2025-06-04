package org.spacebattle.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spacebattle.plugins.PluginComponent;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginYamlUtil {

    public static Map<String, Object> loadYamlFromJar(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry yamlEntry = jar.getJarEntry("plugin.yaml");
            if (yamlEntry == null) throw new RuntimeException("plugin.yaml not found in: " + jarFile.getName());

            try (InputStream yamlStream = jar.getInputStream(yamlEntry)) {
                byte[] bytes = yamlStream.readAllBytes();
                String content = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("YAML content from " + jarFile.getName() + ":\n" + content);

                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                return mapper.readValue(bytes, new TypeReference<>() {});
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void applyDynamicComponents(ClassLoader loader, Map<String, Object> pluginSpec) {
        List<Map<String, String>> dynamicComponents = (List<Map<String, String>>) pluginSpec.get("components");
        if (dynamicComponents == null) return;

        for (Map<String, String> componentDef : dynamicComponents) {
            String className = componentDef.get("class");
            try {
                Class<?> clazz = loader.loadClass(className);
                PluginComponent instance = (PluginComponent) clazz.getDeclaredConstructor().newInstance();
                instance.apply(loader, pluginSpec);
                System.out.println("Dynamic component applied: " + className);
            } catch (Exception e) {
                System.err.println("Failed to apply dynamic PluginComponent: " + className);
                e.printStackTrace();
            }
        }
    }
}
