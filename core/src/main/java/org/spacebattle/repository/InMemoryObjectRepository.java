
package org.spacebattle.repository;

import org.spacebattle.uobject.IUObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Простая реализация ObjectRepository, хранящая объекты в памяти.
 * Используется для тестов, демонстрации и запуска без внешней БД.
 */
public class InMemoryObjectRepository implements ObjectRepository {

    private final Map<String, IUObject> storage = new HashMap<>();

    @Override
    public void save(String id, IUObject object) {
        storage.put(id, object);
    }

    @Override
    public Optional<IUObject> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}
