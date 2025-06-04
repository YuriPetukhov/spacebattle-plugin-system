package org.spacebattle.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * Фабрика для создания {@link URLClassLoader} из JAR-файлов.
 * Интерфейс позволяет реализовывать разные стратегии загрузки классов, например:
 * - Без кэширования
 * - С кэшированием (см. {@link CachingClassLoaderFactory})
 */
public interface ClassLoaderFactory {

    /**
     * Создаёт или возвращает {@link URLClassLoader}, ассоциированный с данным JAR-файлом.
     *
     * @param jarFile файл плагина
     * @return класс-лоадер, способный загружать классы из jarFile
     * @throws IOException в случае ошибки чтения файла
     */
    URLClassLoader create(File jarFile) throws IOException;
}
