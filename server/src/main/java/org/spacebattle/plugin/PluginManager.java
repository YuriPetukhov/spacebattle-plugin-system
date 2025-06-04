package org.spacebattle.plugin;

import java.io.File;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Менеджер плагинов, отвечающий за динамическую загрузку, выгрузку и перезагрузку плагинов.
 * Пока реализация заглушочная, но в перспективе может поддерживать:
 * - Изоляцию ClassLoader'ов
 * - Очистку IoC контейнера
 * - Очистку кэша
 * - Перезагрузку изменённых плагинов
 */
public class PluginManager {

    // Хранит загруженные плагины по имени (или ID) и связанным с ними ClassLoader'ам
    private final Map<String, PluginEntry> loadedPlugins = new HashMap<>();

    /**
     * Загружает плагин из указанного JAR-файла.
     * Пока не реализовано: будет использовать PluginLoader и сохранять информацию в loadedPlugins.
     */
    public void loadPlugin(File jarFile) {
        // TODO: реализовать загрузку через PluginLoader + сохранить в loadedPlugins
    }

    /**
     * Выгружает плагин по имени:
     * - Закрывает ClassLoader
     * - Удаляет все зависимости из IoC (если возможно)
     * - Удаляет плагин из кэша
     */
    public void unloadPlugin(String pluginName) {
        // TODO: выгрузить классы, закрыть ClassLoader, удалить из IoC, очистить кеш
    }

    /**
     * Перезагружает плагин: сначала выгружает старую версию, затем загружает новую.
     */
    public void reloadPlugin(File jarFile) {
        // TODO: unload + load
    }

    /**
     * Возвращает список всех загруженных плагинов по имени.
     */
    public Set<String> listLoadedPlugins() {
        return loadedPlugins.keySet();
    }

    /**
     * Внутренний класс, описывающий загруженный плагин:
     * - JAR-файл
     * - Изолированный ClassLoader
     */
    static class PluginEntry {
        final File jarFile;
        final URLClassLoader classLoader;

        PluginEntry(File jarFile, URLClassLoader classLoader) {
            this.jarFile = jarFile;
            this.classLoader = classLoader;
        }
    }
}
