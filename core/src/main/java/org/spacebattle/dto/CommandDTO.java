package org.spacebattle.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable DTO команды, содержащий действие, идентификатор и параметры.
 * Гарантирует защиту от изменений после создания.
 */
public record CommandDTO(String id, String action, Map<String, Object> params) {

    /**
     * Конструктор с валидацией и защитным копированием.
     */
    public CommandDTO {
        Objects.requireNonNull(action, "Action cannot be null");
        id = (id == null) ? "anon" : id;
        params = (params == null || params.isEmpty())
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(params));
    }

    /**
     * Создаёт CommandDTO из Map (например, из JSON).
     * @throws IllegalArgumentException если отсутствует action или map is null
     */
    public static CommandDTO fromMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Input map cannot be null or empty");
        }

        String action = (String) map.get("action");
        if (action == null || action.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'action' in command map");
        }

        String id = (String) map.getOrDefault("id", "anon");
        Map<String, Object> params = extractParams(map.get("params"));

        return new CommandDTO(id, action, params);
    }

    private static Map<String, Object> extractParams(Object paramsObj) {
        if (paramsObj == null) {
            return Collections.emptyMap();
        }

        if (!(paramsObj instanceof Map<?, ?>)) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) paramsObj).entrySet()) {
            if (entry.getKey() instanceof String key) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    public Map<String, Object> getParamsCopy() {
        return new HashMap<>(params);
    }
}