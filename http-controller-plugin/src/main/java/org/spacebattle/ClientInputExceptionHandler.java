package org.spacebattle;

import org.spacebattle.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Обработчик исключений {@link ClientInputException}, связанных с ошибками во входных данных клиента.
 * Логирует предупреждение с указанием источника и сообщения об ошибке.
 */
public class ClientInputExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientInputExceptionHandler.class);

    /**
     * Обрабатывает исключение, логируя предупреждение с текстом ошибки.
     *
     * @param source имя компонента, где возникла ошибка (например, контроллер)
     * @param e      исключение, которое нужно обработать
     */
    @Override
    public void handle(String source, Exception e) {
        log.warn("Некорректный ввод от клиента в {}: {}", source, e.getMessage());
    }
}
