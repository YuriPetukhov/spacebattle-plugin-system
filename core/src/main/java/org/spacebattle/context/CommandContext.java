package org.spacebattle.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Контекст команды — простой контейнер для хранения произвольных метаданных (например, токена, имени пользователя, источника команды и др.).
 * Обычно используется для передачи контекста через middleware, фильтры или обработчики команд.
 */
public class CommandContext {

    private final Map<String, Object> values = new HashMap<>();

    /**
     * Сохраняет значение по заданному ключу.
     *
     * @param key   Ключ (обычно строка)
     * @param value Объект произвольного типа
     */
    public void put(String key, Object value) {
        values.put(key, value);
    }

    /**
     * Извлекает значение по ключу с приведением к нужному типу.
     *
     * @param key Ключ
     * @return Optional с приведённым значением или пустой, если ключ не найден
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        return Optional.ofNullable((T) values.get(key));
    }

    /**
     * Возвращает все значения контекста как обычную map.
     *
     * @return Map со всеми значениями контекста
     */
    public Map<String, Object> asMap() {
        return values;
    }
}
