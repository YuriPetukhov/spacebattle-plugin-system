package org.spacebattle.setup;

import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.exception.BasicFallbackExceptionHandler;
import org.spacebattle.exception.DefaultExceptionHandlerResolver;

/**
 * Конфигуратор IoC-контейнера, регистрирующий обработчик исключений по умолчанию и его резолвер.
 * Используется для централизованной маршрутизации исключений.
 */
public class IoCExceptionSetup {

    /**
     * Регистрирует в контейнере:
     * <ul>
     *     <li>{@code exception-handler-resolver} — резолвер, возвращающий обработчики по ключу</li>
     * </ul>
     * @param ioc контейнер зависимостей
     */
    public static void setup(IoCContainer ioc) {
        var fallback = new BasicFallbackExceptionHandler();
        var resolver = new DefaultExceptionHandlerResolver(fallback);

        ioc.register("exception-handler-resolver", (ExceptionHandlerResolver) -> resolver);
    }
}
