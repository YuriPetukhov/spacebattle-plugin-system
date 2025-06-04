package org.spacebattle.setup;

import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.DefaultObjectRepository;
import org.spacebattle.repository.ObjectRepository;

/**
 * Конфигуратор IoC-контейнера, создающий и регистрирующий {@link ObjectRepository}.
 * Используется для хранения и поиска IUObject по ID.
 */
public class IoCRepositorySetup {

    /**
     * Регистрирует новую реализацию хранилища объектов.
     * @param ioc контейнер зависимостей
     */
    public static void setup(IoCContainer ioc) {
        ObjectRepository repository = new DefaultObjectRepository();
        ioc.register("object-repository", (ObjectRepository) -> repository);
    }
}