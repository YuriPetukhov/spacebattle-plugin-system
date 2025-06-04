package org.spacebattle.exceptions;

import org.spacebattle.exceptions.ExceptionHandler;

/**
 * Интерфейс, предоставляющий обработчик исключений для конкретного источника.
 * Позволяет выбирать разные обработчики для разных классов или плагинов.
 */
public interface ExceptionHandlerResolver {

    /**
     * Возвращает подходящий ExceptionHandler для указанного источника.
     *
     * @param source имя класса, команды или другого идентификатора
     * @return ExceptionHandler, связанный с этим источником
     */
    ExceptionHandler resolve(String source);
}
