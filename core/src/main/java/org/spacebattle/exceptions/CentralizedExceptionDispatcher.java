package org.spacebattle.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Унифицированный диспетчер обработчиков исключений.
 * Используется для централизованного выбора и вызова соответствующего {@link ExceptionHandler}
 * в зависимости от класса исключения.
 */
public class CentralizedExceptionDispatcher implements ExceptionHandler {

    // Хранилище сопоставлений: класс исключения → соответствующий обработчик
    private final Map<Class<? extends Exception>, ExceptionHandler> handlers = new HashMap<>();
    private final ExceptionHandler defaultHandler;

    /**
     * Создаёт диспетчер с указанным обработчиком по умолчанию.
     *
     * @param defaultHandler обработчик, используемый при отсутствии специфического
     */
    public CentralizedExceptionDispatcher(ExceptionHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    /**
     * Регистрирует обработчик для конкретного класса исключений.
     *
     * @param exClass класс исключения
     * @param handler соответствующий обработчик
     */
    public void registerHandler(Class<? extends Exception> exClass, ExceptionHandler handler) {
        handlers.put(exClass, handler);
    }

    /**
     * Обрабатывает исключение, выбирая соответствующий обработчик по классу.
     * Если подходящего нет — используется defaultHandler.
     *
     * @param source   источник ошибки
     * @param exception исключение для обработки
     */
    @Override
    public void handle(String source, Exception exception) {
        // Поиск обработчика по точному совпадению или суперклассу
        ExceptionHandler handler = Optional.ofNullable(handlers.get(exception.getClass()))
                .orElseGet(() -> handlers.entrySet().stream()
                        .filter(e -> e.getKey().isAssignableFrom(exception.getClass()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(defaultHandler));

        handler.handle(source, exception);
    }

    /**
     * Проверяет, поддерживается ли данный класс исключения.
     *
     * @param clazz класс исключения
     * @return true, если зарегистрирован обработчик
     */
    public boolean supports(Class<? extends Exception> clazz) {
        return handlers.containsKey(clazz);
    }
}
