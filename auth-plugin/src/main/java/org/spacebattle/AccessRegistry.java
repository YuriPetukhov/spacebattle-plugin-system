package org.spacebattle;

import java.util.*;

/**
 * Класс {@code AccessRegistry} представляет собой простой in-memory реестр доступа пользователей к объектам.
 *
 * <p>Используется для хранения информации о том, какие пользователи имеют право управлять какими объектами.</p>
 */
public class AccessRegistry {

    /**
     * Сопоставление: userId → множество objectId, к которым у пользователя есть доступ.
     */
    private final Map<String, Set<String>> userToObjects = new HashMap<>();

    /**
     * Разрешает доступ пользователя к объекту.
     *
     * @param userId   идентификатор пользователя
     * @param objectId идентификатор объекта, к которому предоставляется доступ
     */
    public void allow(String userId, String objectId) {
        userToObjects
                .computeIfAbsent(userId, k -> new HashSet<>())
                .add(objectId);
    }

    /**
     * Проверяет, имеет ли пользователь доступ к заданному объекту.
     *
     * @param userId   идентификатор пользователя
     * @param objectId идентификатор объекта
     * @return {@code true}, если доступ разрешён; {@code false} — если нет
     */
    public boolean isAllowed(String userId, String objectId) {
        return userToObjects.getOrDefault(userId, Set.of()).contains(objectId);
    }
}
