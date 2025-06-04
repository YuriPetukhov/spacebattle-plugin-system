package org.spacebattle;

import com.sun.net.httpserver.HttpServer;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;

/**
 * HTTP-сервер, обрабатывающий команды, отправленные клиентом.
 * Поднимает REST endpoint /command и передаёт запросы в {@link CommandHttpHandler}.
 */
public class CommandControllerServer {

    private final IoC ioc;
    private final ExceptionHandler exceptionHandler;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Конструктор сервера.
     *
     * @param ioc              контейнер зависимостей
     * @param exceptionHandler обработчик исключений
     */
    public CommandControllerServer(IoC ioc, ExceptionHandler exceptionHandler) {
        this.ioc = ioc;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Запускает HTTP-сервер на указанном порту.
     * Регистрирует endpoint /command и запускает сервер.
     *
     * @param port порт, на котором должен быть запущен сервер
     * @throws Exception при ошибке запуска
     */
    public void start(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/command", new CommandHttpHandler(ioc, exceptionHandler, mapper));
        server.setExecutor(null); // Использует default executor
        server.start();
        System.out.println("HTTP-сервер команд запущен на порту " + port);
    }
}
