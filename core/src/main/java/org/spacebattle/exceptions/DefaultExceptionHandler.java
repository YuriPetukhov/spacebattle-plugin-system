package org.spacebattle.exceptions;

/**
 * Стандартный обработчик исключений, который логирует ошибки в stderr.
 * Используется как fallback-обработчик при отсутствии пользовательских.
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    /**
     * Конструктор принимает массив параметров, но не использует их.
     * Это нужно для совместимости с IoC-интерфейсом.
     *
     * @param objects аргументы, передаваемые через IoC (игнорируются)
     */
    public DefaultExceptionHandler(Object[] objects) {
    }

    /**
     * Обрабатывает исключение, логируя его в stderr.
     *
     * @param className имя класса, в котором произошло исключение
     * @param e         само исключение
     */
    @Override
    public void handle(String className, Exception e) {
        System.err.printf("[EXCEPTION] %s: %s%n", className, e.getMessage());
        e.printStackTrace(System.err);
    }
}
