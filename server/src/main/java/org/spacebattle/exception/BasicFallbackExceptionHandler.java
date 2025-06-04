package org.spacebattle.exception;

import org.spacebattle.exceptions.ExceptionHandler;

/**
 * Базовый обработчик исключений по умолчанию.
 * Используется как резервный (fallback) вариант, если не определён конкретный обработчик.
 * Просто выводит информацию об ошибке в стандартный поток ошибок.
 */
public class BasicFallbackExceptionHandler implements ExceptionHandler {

    /**
     * Обрабатывает исключение, выводя его в System.err.
     *
     * @param source источник исключения (например, имя команды)
     * @param e      само исключение
     */
    @Override
    public void handle(String source, Exception e) {
        System.err.printf("[EXCEPTION] Source: %s → %s: %s%n",
                source, e.getClass().getSimpleName(), e.getMessage());
    }
}
