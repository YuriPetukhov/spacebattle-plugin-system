package org.spacebattle.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация {@link ClassLoaderFactory}, которая кэширует {@link URLClassLoader} для каждого JAR-файла.
 * Это позволяет избежать повторного создания загрузчиков для одного и того же файла.
 */
public class CachingClassLoaderFactory implements ClassLoaderFactory {

    // Кэш для хранения загрузчиков по пути к JAR
    private final Map<String, URLClassLoader> cache = new HashMap<>();

    /**
     * Возвращает существующий загрузчик из кэша или создаёт новый, если он ещё не был создан.
     *
     * @param jarFile файл JAR, из которого загружаются классы
     * @return URLClassLoader, загружающий классы из jarFile
     * @throws IOException если невозможно получить путь к JAR
     */
    @Override
    public URLClassLoader create(File jarFile) throws IOException {
        String key = jarFile.getCanonicalPath();
        return cache.computeIfAbsent(key, k -> {
            try {
                return new URLClassLoader(new URL[]{jarFile.toURI().toURL()},
                        Thread.currentThread().getContextClassLoader());
            } catch (IOException e) {
                throw new RuntimeException(e); // пробрасываем как непроверяемое исключение
            }
        });
    }
}
