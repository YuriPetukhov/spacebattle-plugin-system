package org.spacebattle.factory;

import org.spacebattle.uobject.IUObject;

/**
 * Интерфейс фабрики объектов, предоставляющей IUObject по идентификатору.
 * Может использоваться как кэш или делегат создания.
 */
public interface ObjectFactory {

    /**
     * Возвращает объект по ID, создавая его при необходимости.
     *
     * @param id уникальный идентификатор объекта
     * @return IUObject (существующий или новый)
     */
    IUObject getOrCreate(String id);
}
