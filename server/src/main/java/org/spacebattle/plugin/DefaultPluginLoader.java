package org.spacebattle.plugin;

import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.util.PluginYamlUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * DefaultPluginLoader — реализация интерфейса PluginLoader.
 * Отвечает за загрузку плагинов из JAR-файлов.
 * Поддерживает кэширование загрузчиков классов и динамическую инициализацию компонентов.
 */
public class DefaultPluginLoader implements PluginLoader {

    private final PluginScanner scanner;
    private final PluginProcessor processor;
    private final List<PluginComponent> components;
    private final Map<String, URLClassLoader> classLoaderCache = new HashMap<>();

    public DefaultPluginLoader(PluginScanner scanner, PluginProcessor processor, List<PluginComponent> components) {
        this.scanner = scanner;
        this.processor = processor;
        this.components = components;
    }

    /**
     * Загружает все плагины из указанной директории.
     *
     * @param pluginDir директория с JAR-плагинами
     * @throws Exception если произойдёт ошибка при загрузке
     */
    @Override
    public void loadAllFrom(File pluginDir) throws Exception {
        for (File jar : scanner.findJars(pluginDir)) {
            processor.process(jar);
        }
    }

    /**
     * Загружает один JAR-плагин и инициализирует его компоненты.
     *
     * @param jarFile путь к JAR-файлу
     * @throws Exception если произошла ошибка при загрузке
     */
    @Override
    public void loadPlugin(File jarFile) throws Exception {
        System.out.println("Loading plugin: " + jarFile.getName());

        try (JarFile jar = new JarFile(jarFile)) {
            jar.stream().map(JarEntry::getName).forEach(System.out::println);
        }

        URLClassLoader loader = createIsolatedClassLoader(jarFile);

        Map<String, Object> pluginData = PluginYamlUtil.loadYamlFromJar(jarFile);
        Map<String, Object> pluginSpec = (Map<String, Object>) pluginData.get("plugin");

        for (PluginComponent component : components) {
            component.apply(loader, pluginData);
        }

        PluginYamlUtil.applyDynamicComponents(loader, pluginSpec);
    }

    /**
     * Создаёт изолированный загрузчик классов для плагина.
     *
     * @param jarFile путь к JAR-файлу
     * @return URLClassLoader для загрузки плагина
     * @throws Exception в случае ошибки
     */
    private URLClassLoader createIsolatedClassLoader(File jarFile) throws Exception {
        String key = jarFile.getCanonicalPath();

        if (classLoaderCache.containsKey(key)) {
            return classLoaderCache.get(key);
        }

        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[]{jarUrl}, Thread.currentThread().getContextClassLoader());
        classLoaderCache.put(key, cl);
        return cl;
    }
}
