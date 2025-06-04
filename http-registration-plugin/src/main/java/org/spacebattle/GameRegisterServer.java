package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.net.InetSocketAddress;

/**
 * HTTP-сервер для обработки регистрации игроков в игре.
 * Создаёт эндпоинт /register и запускает обработку через {@link GameRegisterHandler}.
 */
public class GameRegisterServer {

    private final IoC ioc;
    private final ExceptionHandler exceptionHandler;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Конструктор сервера.
     *
     * @param ioc              контейнер зависимостей
     * @param exceptionHandler обработчик исключений
     */
    public GameRegisterServer(IoC ioc, ExceptionHandler exceptionHandler) {
        this.ioc = ioc;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Запускает HTTP-сервер на указанном порту.
     * Создаёт контекст /register и обрабатывает запросы через {@link GameRegisterHandler}.
     *
     * @param port порт, на котором будет слушать сервер
     * @throws Exception при ошибке запуска
     */
    public void start(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/register", new GameRegisterHandler(ioc, exceptionHandler, mapper));
        server.setExecutor(null); // создаёт default executor
        server.start();
        System.out.println("HTTP-сервер регистрации игры запущен на порту " + port);
    }
}
