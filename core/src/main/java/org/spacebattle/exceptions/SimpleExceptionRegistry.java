package org.spacebattle.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Простая реализация реестра обработчиков исключений.
 * Позволяет регистрировать и извлекать {@link ExceptionHandler} по строковому имени.
 * Если обработчик не найден, возвращается fallback-обработчик.
 */
public class SimpleExceptionRegistry {

    private final Map<String, ExceptionHandler> handlers = new HashMap<>();
    private ExceptionHandler fallback = new DefaultExceptionHandler(new Object[0]);

    /**
     * Регистрирует обработчик исключений с заданным именем.
     *
     * @param name    имя обработчика
     * @param handler экземпляр {@link ExceptionHandler}
     */
    public void register(String name, ExceptionHandler handler) {
        handlers.put(name, handler);
    }

    /**
     * Извлекает обработчик по имени. Если не найден — возвращает fallback.
     *
     * @param name имя обработчика
     * @return найденный обработчик или fallback
     */
    public ExceptionHandler resolve(String name) {
        return handlers.getOrDefault(name, fallback);
    }

    /**
     * Устанавливает fallback-обработчик, возвращаемый, если по имени ничего не найдено.
     *
     * @param fallback новый fallback-обработчик
     */
    public void setFallback(ExceptionHandler fallback) {
        this.fallback = fallback;
    }
}
