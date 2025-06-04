package org.spacebattle.repository;

import org.spacebattle.uobject.IUObject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация ObjectRepository, хранящая объекты IUObject в памяти с потокобезопасной ConcurrentHashMap.
 */
public class DefaultObjectRepository implements ObjectRepository {

    private final Map<String, IUObject> storage = new ConcurrentHashMap<>();

    /**
     * Сохраняет объект под заданным идентификатором.
     * @param id идентификатор объекта
     * @param object объект IUObject
     */
    @Override
    public void save(String id, IUObject object) {
        storage.put(id, object);
    }

    /**
     * Находит объект по идентификатору.
     * @param id идентификатор
     * @return Optional с объектом или пустой, если не найден
     */
    @Override
    public Optional<IUObject> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Проверяет, существует ли объект с заданным идентификатором.
     * @param id идентификатор
     * @return true, если объект существует
     */
    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }

    /**
     * Удаляет объект по идентификатору.
     * @param id идентификатор
     */
    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}