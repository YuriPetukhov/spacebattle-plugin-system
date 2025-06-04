package org.spacebattle.repository;

import org.spacebattle.uobject.IUObject;

import java.util.Optional;

/**
 * Репозиторий для хранения и доступа к объектам IUObject по их идентификатору.
 * Используется как абстракция над хранилищем игровых объектов, конфигураций и т.п.
 */
public interface ObjectRepository {

    /**
     * Сохраняет объект в репозитории по заданному ID.
     * Если объект с таким ID уже существует — он перезаписывается.
     *
     * @param id     уникальный идентификатор объекта
     * @param object объект, реализующий интерфейс IUObject
     */
    void save(String id, IUObject object);

    /**
     * Возвращает объект по ID, если он существует.
     *
     * @param id идентификатор объекта
     * @return Optional с объектом или пустой, если не найден
     */
    Optional<IUObject> findById(String id);

    /**
     * Проверяет наличие объекта по ID.
     *
     * @param id идентификатор
     * @return true, если объект с таким ID существует
     */
    boolean exists(String id);

    /**
     * Удаляет объект по ID, если он существует.
     *
     * @param id идентификатор объекта
     */
    void delete(String id);
}
