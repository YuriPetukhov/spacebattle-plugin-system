package org.spacebattle.plugin;

import java.io.File;

/**
 * Интерфейс PluginLoader определяет контракт для загрузчиков плагинов.
 * Он предоставляет методы для загрузки всех плагинов из директории и отдельного плагина из JAR-файла.
 */
public interface PluginLoader {

    /**
     * Загружает все плагины из указанной директории.
     * Может использовать сканер для поиска JAR-файлов и инициировать их обработку.
     *
     * @param pluginDir директория, содержащая JAR-файлы плагинов
     * @throws Exception если возникает ошибка при обработке плагинов
     */
    void loadAllFrom(File pluginDir) throws Exception;

    /**
     * Загружает конкретный JAR-файл плагина и инициализирует его компоненты.
     *
     * @param jarFile JAR-файл с плагином
     * @throws Exception если возникает ошибка при загрузке или инициализации
     */
    void loadPlugin(File jarFile) throws Exception;
}
