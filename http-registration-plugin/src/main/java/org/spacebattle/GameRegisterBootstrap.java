package org.spacebattle;

import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

/**
 * Бутстрап-класс для запуска HTTP-сервера регистрации игры.
 * Использует IoC-контейнер для получения зависимостей и запуска {@link GameRegisterServer}.
 *
 * <p>Основные задачи:</p>
 * <ul>
 *     <li>Разрешить зависимости через IoC (контейнер и обработчик исключений).</li>
 *     <li>Создать и запустить HTTP-сервер регистрации объектов в игре на заданном порту.</li>
 *     <li>Обработать возможные ошибки при запуске сервера с помощью {@link ExceptionHandler}.</li>
 * </ul>
 *
 * <p>Порт может быть задан через системное свойство {@code game.register.port}. По умолчанию используется 8084.</p>
 */
public class GameRegisterBootstrap {

    /**
     * IoC-контейнер для разрешения зависимостей.
     */
    private IoC ioc;

    /**
     * Обработчик исключений, используемый для логирования и управления ошибками.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * Устанавливает внешний IoC-контейнер. Обычно используется для тестирования.
     *
     * @param ioc внешний контейнер зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Запускает сервер регистрации игры.
     * Получает зависимости из IoC-контейнера, создает {@link GameRegisterServer} и запускает его
     * на порту, указанном в системных свойствах или по умолчанию (8084).
     */
    public void run() {
        this.ioc = (IoC) ioc.resolve("ioc");
        this.exceptionHandler = (ExceptionHandler) ioc.resolve("exception-handler");

        int port = getConfiguredPort();

        GameRegisterServer server = new GameRegisterServer(ioc, exceptionHandler);
        try {
            server.start(port);
        } catch (Exception e) {
            exceptionHandler.handle(getClass().getSimpleName(), e);
        }
    }

    /**
     * Получает порт из системного свойства {@code game.register.port} или возвращает значение по умолчанию.
     *
     * @return порт для запуска сервера
     */
    private int getConfiguredPort() {
        String portProperty = System.getProperty("game.register.port");
        try {
            return portProperty != null ? Integer.parseInt(portProperty) : 8084;
        } catch (NumberFormatException e) {
            return 8084;
        }
    }
}
