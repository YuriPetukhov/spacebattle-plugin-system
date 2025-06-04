package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.middleware.Middleware;

/**
 * Middleware, оборачивающее выполнение команды в блок try-catch.
 * <p>
 * При возникновении исключения оно передаётся обработчику {@link ExceptionHandler}.
 * Используется для централизованной обработки ошибок команд.
 */
public class ExceptionCatchingMiddleware implements Middleware {

    private final ExceptionHandler handler;

    /**
     * Создаёт middleware с указанным обработчиком исключений.
     *
     * @param handler обработчик, который будет вызван при возникновении исключения
     */
    public ExceptionCatchingMiddleware(ExceptionHandler handler) {
        this.handler = handler;
    }

    /**
     * Оборачивает выполнение команды, добавляя обработку исключений.
     * Если во время выполнения возникнет исключение, оно будет передано в {@link ExceptionHandler}.
     *
     * @param next команда, которую нужно выполнить
     * @return новая команда с обработкой ошибок
     */
    @Override
    public Command wrap(Command next) {
        return () -> {
            try {
                next.execute();
            } catch (Exception e) {
                handler.handle(next.getClass().getSimpleName(), e);
            }
        };
    }
}
