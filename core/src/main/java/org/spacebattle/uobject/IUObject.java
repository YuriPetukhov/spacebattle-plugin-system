package org.spacebattle.uobject;

import java.util.Map;
import java.util.Optional;

/**
 * Универсальный интерфейс объекта, хранящего свойства по ключу.
 * Ключи представлены строками, значения — любыми объектами.
 *
 * Используется как обёртка над игровыми сущностями, объектами конфигурации и т.п.
 */
public interface IUObject {

    /**
     * Устанавливает свойство по ключу.
     *
     * @param key   имя свойства
     * @param value значение (любой тип)
     * @param <T>   тип значения
     */
    <T> void setProperty(String key, T value);

    /**
     * Возвращает значение свойства по ключу (если есть).
     *
     * @param key имя свойства
     * @param <T> ожидаемый тип
     * @return Optional с результатом или пустой, если нет значения
     */
    <T> Optional<T> getProperty(String key);

    Map<String, Object> getAllProperties();

}
