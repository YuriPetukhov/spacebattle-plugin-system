package org.spacebattle.ioc;

import java.util.function.Function;

/**
 * Интерфейс для контейнера инверсии управления (IoC).
 * Предоставляет метод для разрешения зависимости по ключу.
 */
public interface IoC {

    void register(String key, Function<Object[], Object> factory);

    /**
     * Возвращает зарегистрированный объект по ключу,
     * передавая ему аргументы, если они требуются.
     *
     * @param key   ключ зависимости (например, "service:move")
     * @param args  аргументы для фабрики
     * @return экземпляр зависимости
     */
    Object resolve(String key, Object... args);

    boolean contains(String key);
}
