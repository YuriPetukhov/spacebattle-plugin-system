package org.spacebattle;

/**
 * Исключение, выбрасываемое при некорректном вводе от клиента.
 * Может обрабатываться пользовательским ExceptionHandler.
 */
public class ClientInputException extends RuntimeException {

    public ClientInputException(String message) {
        super(message);
    }

    public ClientInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
