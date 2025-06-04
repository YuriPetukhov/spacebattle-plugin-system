package org.spacebattle.setup;

import org.spacebattle.factory.DefaultObjectFactory;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.factory.ObjectFactory;
import org.spacebattle.repository.ObjectRepository;

/**
 * Конфигуратор IoC-контейнера, регистрирующий {@link ObjectFactory}.
 * Используется для получения или создания IUObject по ID.
 */
public class IoCObjectSetup {

    /**
     * Выполняет настройку фабрики объектов, используя ранее зарегистрированный репозиторий объектов.
     * @param ioc контейнер зависимостей
     */
    public static void setup(IoCContainer ioc) {
        ObjectRepository repository = (ObjectRepository) ioc.resolve("object-repository");
        ObjectFactory factory = new DefaultObjectFactory(ioc, repository);
        ioc.register("object-factory", (ObjectFactory) -> factory);
    }
}