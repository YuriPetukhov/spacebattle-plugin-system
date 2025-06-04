package org.spacebattle.plugin;

import java.io.File;
import java.util.List;

/**
 * Интерфейс PluginScanner отвечает за поиск jar-файлов плагинов
 * в указанной директории.
 */
public interface PluginScanner {
    /**
     * Находит все jar-файлы в указанной директории.
     *
     * @param directory директория поиска
     * @return список jar-файлов
     */
    List<File> findJars(File directory);
}