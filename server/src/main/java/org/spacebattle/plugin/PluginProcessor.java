package org.spacebattle.plugin;

import java.io.File;

/**
 * Интерфейс PluginProcessor описывает контракт обработки плагина.
 * Реализация должна уметь загружать и инициализировать компоненты плагина из jar-файла.
 */
public interface PluginProcessor {
    /**
     * Обрабатывает указанный jar-файл, извлекая и активируя описанные компоненты.
     *
     * @param jarFile путь к jar-файлу
     * @throws Exception в случае ошибки обработки
     */
    void process(File jarFile) throws Exception;
}