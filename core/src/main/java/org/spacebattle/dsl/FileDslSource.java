package org.spacebattle.dsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Реализация {@link DslSource}, загружающая данные из локального файла.
 * <p>
 * Используется в системах, где конфигурация DSL хранится в виде файлов
 * (например, YAML-файлы с описанием объектов или команд).
 */
public class FileDslSource implements DslSource {

    private final File file;

    /**
     * Конструктор, принимающий файл, откуда будет читаться DSL.
     *
     * @param file файл с DSL-описанием
     */
    public FileDslSource(File file) {
        this.file = file;
    }

    /**
     * Открывает поток чтения из указанного файла.
     *
     * @return {@link InputStream} для чтения содержимого файла
     * @throws Exception если файл не существует или произошла ошибка чтения
     */
    @Override
    public InputStream openStream() throws Exception {
        return new FileInputStream(file);
    }
}
