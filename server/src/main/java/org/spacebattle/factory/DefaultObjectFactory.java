package org.spacebattle.factory;

import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

/**
 * Фабрика объектов по умолчанию.
 * При запросе по ID:
 * - возвращает существующий объект из IoC, если он есть
 * - иначе создаёт новый {@link DefaultUObject}, сохраняет в репозиторий и регистрирует в IoC
 */
public class DefaultObjectFactory implements ObjectFactory {
    private final IoCContainer ioc;
    private final ObjectRepository objectRepository;

    /**
     * Конструктор фабрики
     *
     * @param ioc              контейнер зависимостей
     * @param objectRepository репозиторий объектов
     */
    public DefaultObjectFactory(IoCContainer ioc, ObjectRepository objectRepository) {
        this.ioc = ioc;
        this.objectRepository = objectRepository;
    }

    /**
     * Возвращает объект с заданным ID из IoC или создаёт новый.
     *
     * @param id идентификатор объекта
     * @return IUObject — либо существующий, либо новый
     */
    @Override
    public IUObject getOrCreate(String id) {
        String key = "object:" + id;
        try {
            return (IUObject) ioc.resolve(key);
        } catch (Exception e) {
            IUObject obj = new DefaultUObject();
            objectRepository.save(id, obj);
            ioc.register(key, args -> obj);
            System.out.println("Создан и зарегистрирован объект: " + id);
            return obj;
        }
    }
}
