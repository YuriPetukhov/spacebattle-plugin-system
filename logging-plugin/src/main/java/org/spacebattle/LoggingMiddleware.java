package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.middleware.Middleware;

/**
 * Middleware, добавляющий логирование до и после выполнения команды.
 * Не выводит ничего, если логика не подключена.
 * Для использования java -Dlog.commands -jar server.jar
 */
public class LoggingMiddleware implements Middleware {
    @Override
    public Command wrap(Command next) {
        return () -> {
            if (System.getProperty("log.commands") != null) {
                System.out.println("[log] → Executing command: " + next.getClass().getSimpleName());
            }
            next.execute();
        };
    }
}