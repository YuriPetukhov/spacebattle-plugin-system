package org.spacebattle.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Утилита для загрузки и парсинга YAML-файла plugin.yaml из JAR-файлов.
 * Предназначена для систем плагинов, где plugin.yaml описывает структуру плагина.
 */
public class PluginYamlUtil {

    private static final String PLUGIN_YAML = "plugin.yaml";

    /**
     * Загружает файл plugin.yaml из корня JAR-файла и парсит его содержимое.
     *
     * @param jarFile JAR-файл с плагином
     * @return Map, содержащая структуру plugin.yaml
     * @throws Exception если файл не найден, не читается или содержит некорректный YAML
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYamlFromJar(java.io.File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry(PLUGIN_YAML);
            if (entry == null) {
                throw new IllegalArgumentException("plugin.yaml not found in " + jarFile.getName());
            }

            try (InputStream input = jar.getInputStream(entry)) {
                Yaml yaml = new Yaml();
                return yaml.load(input);
            }
        }
    }
}
