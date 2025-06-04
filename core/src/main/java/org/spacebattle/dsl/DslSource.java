package org.spacebattle.dsl;

import java.io.InputStream;

/**
 * Интерфейс источника данных DSL-конфигурации.
 * <p>
 * Определяет контракт на открытие потока данных, откуда может быть прочтён YAML-файл
 * или другой формат конфигурации. Может быть реализован для различных источников:
 * файл, URL, строка и т.д.
 * </p>
 */
public interface DslSource {

    /**
     * Открывает поток ввода для чтения содержимого DSL.
     *
     * @return {@link InputStream} для чтения данных
     * @throws Exception если произошла ошибка при открытии потока
     */
    InputStream openStream() throws Exception;
}
