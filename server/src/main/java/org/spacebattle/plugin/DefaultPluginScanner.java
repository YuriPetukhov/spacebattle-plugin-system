package org.spacebattle.plugin;

import java.io.File;
import java.util.List;

/**
 * Реализация PluginScanner, которая ищет все jar-файлы в директории.
 */
public class DefaultPluginScanner implements PluginScanner {
    /**
     * Возвращает список файлов с расширением .jar в указанной директории.
     *
     * @param dir директория для сканирования
     * @return список найденных jar-файлов
     */
    @Override
    public List<File> findJars(File dir) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        return files != null ? List.of(files) : List.of();
    }
}