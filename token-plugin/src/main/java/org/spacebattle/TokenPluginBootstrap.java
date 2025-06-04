package org.spacebattle;

import org.spacebattle.ioc.IoC;

/**
 * Класс {@code TokenPluginBootstrap} отвечает за инициализацию плагина верификации токена.
 *
 * <p>Этот класс используется как точка входа при подключении плагина проверки токена.
 * Он извлекает URL сервиса верификации токенов из системных свойств, создаёт клиент
 * {@link TokenVerifierClient}, и регистрирует соответствующую функцию в IoC-контейнере
 * с помощью {@link TokenVerifierRegistrar}.</p>
 */
public class TokenPluginBootstrap {

    /**
     * Ссылка на контейнер внедрения зависимостей (Inversion of Control).
     */
    private IoC ioc;

    /**
     * Устанавливает IoC-контейнер, в который будут регистрироваться компоненты плагина.
     *
     * @param ioc контейнер внедрения зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Инициализирует плагин верификации токена:
     * <ul>
     *     <li>Читает URL сервиса верификации из системного свойства {@code token.verifier.url}.</li>
     *     <li>Создаёт {@link TokenVerifierClient} для взаимодействия с сервисом верификации.</li>
     *     <li>Регистрирует функцию верификации токенов в IoC-контейнере под именем {@code "token-verifier"}.</li>
     * </ul>
     */
    public void run() {
        System.out.println("TokenPluginBootstrap initializing...");

        // Получение URL сервиса верификации токенов, с возможностью переопределения через системное свойство
        String url = System.getProperty("token.verifier.url", "http://auth-service:8080/auth/verify");

        // Создание клиента для HTTP-взаимодействия с сервисом верификации токенов
        TokenVerifierClient client = new TokenVerifierClient(url);

        // Регистрация функции верификации токенов в IoC
        TokenVerifierRegistrar registrar = new TokenVerifierRegistrar(ioc, client);
        registrar.register();
    }
}
