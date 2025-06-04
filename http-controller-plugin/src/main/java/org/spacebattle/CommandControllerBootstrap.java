package org.spacebattle;

import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;
import org.spacebattle.plugins.PluginComponent;

import java.util.Map;

/**
 * Класс начальной инициализации для запуска {@link CommandControllerServer}.
 * <p>
 * Поддерживает внедрение IoC-контейнера и обработчика исключений. После настройки вызывает запуск HTTP-сервера.
 */
public class CommandControllerBootstrap {

    private IoC ioc;
    private ExceptionHandler exceptionHandler;

    /**
     * Устанавливает IoC-контейнер для передачи в {@link CommandControllerServer}.
     *
     * @param ioc контейнер зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    public void run() {
        this.ioc = (IoC) ioc.resolve("ioc");
        this.exceptionHandler = (ExceptionHandler) ioc.resolve("exception-handler");

        CommandControllerServer server = new CommandControllerServer(ioc, exceptionHandler);
        try {
            server.start(8080);
        } catch (Exception e) {
            exceptionHandler.handle(getClass().getSimpleName(), e);
        }
    }
}
