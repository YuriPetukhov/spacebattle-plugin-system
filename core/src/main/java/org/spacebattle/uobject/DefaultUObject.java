package org.spacebattle.uobject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Простая реализация IUObject на базе HashMap.
 * Позволяет сохранять и извлекать свойства по строковому ключу.
 */
public class DefaultUObject implements IUObject {

    private final Map<String, Object> properties = new HashMap<>();

    @Override
    public <T> void setProperty(String key, T value) {
        properties.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getProperty(String key) {
        return Optional.ofNullable((T) properties.get(key));
    }

    @Override
    public Map<String, Object> getAllProperties() {
        return new HashMap<>(properties);
    }
}
