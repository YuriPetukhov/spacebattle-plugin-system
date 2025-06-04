package org.spacebattle.execution;

import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Последовательная очередь выполнения команд.
 * Все команды выполняются в том потоке, из которого была вызвана {@link #submit(Command)}.
 * Исключения обрабатываются через переданный {@link ExceptionHandler}.
 */
public class ExecutionQueue {

    private final Queue<Command> queue = new ConcurrentLinkedQueue<>();
    private final ExceptionHandler exceptionHandler;
    private boolean processing = false;

    /**
     * Создаёт ExecutionQueue с указанным обработчиком исключений.
     *
     * @param exceptionHandler обработчик исключений
     */
    public ExecutionQueue(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Добавляет команду в очередь и запускает обработку, если она ещё не идёт.
     *
     * @param command команда для выполнения
     */
    public synchronized void submit(Command command) {
        queue.offer(command);
        if (!processing) {
            processing = true;
            processNext();
        }
    }

    /**
     * Последовательно извлекает и выполняет команды из очереди.
     * Обработка исключений делегируется exceptionHandler.
     */
    private void processNext() {
        while (!queue.isEmpty()) {
            Command command = queue.poll();
            if (command != null) {
                try {
                    command.execute();
                } catch (Exception e) {
                    exceptionHandler.handle(command.getClass().getSimpleName(), e);
                }
            }
        }
        processing = false;
    }
}
