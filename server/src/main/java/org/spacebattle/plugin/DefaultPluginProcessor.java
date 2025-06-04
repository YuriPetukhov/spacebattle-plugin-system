package org.spacebattle.plugin;

import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.util.PluginYamlUtil;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

/**
 * Реализация PluginProcessor, которая использует список компонентов и фабрику загрузчиков
 * для обработки jar-файлов плагинов.
 */
public class DefaultPluginProcessor implements PluginProcessor {
    private final List<PluginComponent> components;
    private final ClassLoaderFactory loaderFactory;

    public DefaultPluginProcessor(List<PluginComponent> components, ClassLoaderFactory loaderFactory) {
        this.components = components;
        this.loaderFactory = loaderFactory;
    }

    /**
     * Загружает jar-файл и применяет все зарегистрированные компоненты плагина,
     * а затем выполняет динамическую инициализацию.
     *
     * @param jarFile путь к jar-файлу плагина
     * @throws Exception в случае ошибки загрузки
     */
    @Override
    public void process(File jarFile) throws Exception {
        URLClassLoader loader = loaderFactory.create(jarFile);
        Map<String, Object> yaml = PluginYamlUtil.loadYamlFromJar(jarFile);
        Map<String, Object> pluginSpec = (Map<String, Object>) yaml.get("plugin");

        for (PluginComponent c : components) {
            c.apply(loader, yaml);
        }

        PluginYamlUtil.applyDynamicComponents(loader, pluginSpec);
    }
}
