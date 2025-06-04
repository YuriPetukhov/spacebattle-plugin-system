package org.spacebattle.exception;

import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandlerResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация {@link ExceptionHandlerResolver}, которая позволяет
 * регистрировать конкретные обработчики исключений по ключу (например, имени команды).
 * При отсутствии зарегистрированного обработчика возвращается fallback-хендлер.
 */
public class DefaultExceptionHandlerResolver implements ExceptionHandlerResolver {

    private final ExceptionHandler fallback;
    private final Map<String, ExceptionHandler> map = new HashMap<>();

    /**
     * Конструктор.
     *
     * @param fallback резервный обработчик, если ключ не найден
     */
    public DefaultExceptionHandlerResolver(ExceptionHandler fallback) {
        this.fallback = fallback;
    }

    /**
     * Регистрирует обработчик исключений для заданного источника.
     *
     * @param key     источник (например, имя команды)
     * @param handler соответствующий обработчик
     */
    public void register(String key, ExceptionHandler handler) {
        map.put(key, handler);
    }

    /**
     * Возвращает обработчик исключений по ключу.
     * Если не найден, возвращается fallback-хендлер.
     *
     * @param source источник (например, имя команды)
     * @return обработчик исключений
     */
    @Override
    public ExceptionHandler resolve(String source) {
        return map.getOrDefault(source, fallback);
    }
}
