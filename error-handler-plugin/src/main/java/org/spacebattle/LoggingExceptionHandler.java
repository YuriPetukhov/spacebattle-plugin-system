package org.spacebattle;

import org.spacebattle.exceptions.ExceptionHandler;

/**
 * Обработчик исключений, логирующий сообщения об ошибках в System.err.
 * Формат вывода: [EXCEPTION] Command: <source> → <ExceptionClass>: <message>
 * Используется для отладки и регистрации сбоев выполнения команд.
 */
public class LoggingExceptionHandler implements ExceptionHandler {

    /**
     * Обрабатывает исключение, записывая его в стандартный поток ошибок.
     * @param source источник (например, имя команды или компонента), где произошло исключение
     * @param e само исключение, которое нужно залогировать
     */
    @Override
    public void handle(String source, Exception e) {
        System.err.printf("[EXCEPTION] Command: %s → %s: %s%n",
                source,
                e.getClass().getSimpleName(),
                e.getMessage());
    }
}
