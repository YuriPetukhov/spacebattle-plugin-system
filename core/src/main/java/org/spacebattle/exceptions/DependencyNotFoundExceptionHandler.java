package org.spacebattle.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Обработчик исключений типа {@link DependencyNotFoundException}.
 * Логирует сообщение об отсутствии зависимости с указанием класса-источника.
 */
public class DependencyNotFoundExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DependencyNotFoundExceptionHandler.class);

    /**
     * Обрабатывает исключение, логируя сообщение об отсутствии зависимости.
     *
     * @param className имя класса, в котором возникла ошибка
     * @param e         исключение типа {@link DependencyNotFoundException}
     */
    @Override
    public void handle(String className, Exception e) {
        log.error("[DependencyNotFound] В классе {} не найдена зависимость: {}", className, e.getMessage(), e);
    }
}
