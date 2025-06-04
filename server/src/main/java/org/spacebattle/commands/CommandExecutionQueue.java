package org.spacebattle.commands;

import org.spacebattle.exceptions.ExceptionHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Очередь команд на исполнение в отдельном потоке.
 * Обрабатывает команды последовательно, в фоновом потоке.
 * Поддерживает обработку исключений и мягкое завершение.
 */
public class CommandExecutionQueue {

    private final Queue<Command> queue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExceptionHandler exceptionHandler;
    private volatile boolean running = true;

    /**
     * Создаёт очередь и запускает фоновый поток обработки.
     *
     * @param exceptionHandler обработчик исключений для команд
     */
    public CommandExecutionQueue(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        executor.submit(this::processLoop);
    }

    /**
     * Добавляет команду в очередь на исполнение.
     *
     * @param command команда для выполнения
     */
    public void submit(Command command) {
        queue.add(command);
    }

    /**
     * Основной цикл обработки: извлекает команды из очереди и выполняет их.
     * Если очередь пуста — ждёт.
     */
    private void processLoop() {
        while (running) {
            Command command = queue.poll();
            if (command != null) {
                try {
                    command.execute();
                } catch (Exception e) {
                    exceptionHandler.handle(command.getClass().getSimpleName(), e);
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Останавливает выполнение команд и завершает поток.
     */
    public void stop() {
        running = false;
        executor.shutdown();
    }
}
