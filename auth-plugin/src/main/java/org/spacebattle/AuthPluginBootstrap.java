package org.spacebattle;

import org.spacebattle.ioc.IoC;
import org.spacebattle.middleware.Middleware;

import java.util.function.Function;

/**
 * Класс {@code AuthPluginBootstrap} отвечает за инициализацию и регистрацию компонентов авторизационного плагина.
 *
 * <p>При запуске регистрирует:
 * <ul>
 *     <li>{@link AccessRegistry} — централизованный реестр прав доступа объектов.</li>
 *     <li>{@link AuthMiddleware} — middleware, проверяющее, имеет ли пользователь доступ к объекту.</li>
 * </ul>
 * Компоненты регистрируются в {@link IoC} контейнер под ключами {@code "access-registry"} и {@code "middleware:auth"} соответственно.
 */
public class AuthPluginBootstrap {

    /**
     * Контейнер внедрения зависимостей, в который регистрируются компоненты.
     */
    private IoC ioc;

    /**
     * Устанавливает контейнер внедрения зависимостей (IoC).
     *
     * @param ioc IoC-контейнер
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Выполняет инициализацию авторизационного плагина:
     * <ul>
     *     <li>Создаёт и регистрирует {@link AccessRegistry}.</li>
     *     <li>Создаёт и регистрирует {@link AuthMiddleware}, использующее этот реестр.</li>
     * </ul>
     */
    public void run() {
        System.out.println("AuthPluginBootstrap initializing...");

        // Регистрируем реестр доступа
        AccessRegistry registry = new AccessRegistry();
        ioc.resolve("IoC.Register", "access-registry", (Function<Object[], Object>) args -> registry);

        // Регистрируем middleware авторизации, использующее AccessRegistry
        Middleware authMiddleware = new AuthMiddleware(registry);
        ioc.resolve("IoC.Register", "middleware:auth", (Function<Object[], Object>) args -> authMiddleware);

        System.out.println("AuthMiddleware и AccessRegistry зарегистрированы");
    }
}
