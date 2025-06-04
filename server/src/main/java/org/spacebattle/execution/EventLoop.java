package org.spacebattle.execution;

import lombok.Getter;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;

import java.util.concurrent.*;

/**
 * Класс EventLoop реализует многопоточную очередь команд с управлением жизненным циклом.
 * Потоки извлекают команды из общей очереди и обрабатывают их последовательно.
 * В случае ошибок используется обработчик исключений.
 */
@Getter
public class EventLoop {

    private final BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
    private final ExecutorService workers;
    private final ExceptionHandler exceptionHandler;
    private Runnable idleBehaviour = () -> System.out.println("Очередь пуста. Стандартное поведение.");

    /**
     * Конструктор EventLoop
     *
     * @param threads           количество рабочих потоков
     * @param exceptionHandler  обработчик исключений
     */
    public EventLoop(int threads, ExceptionHandler exceptionHandler) {
        this.workers = Executors.newFixedThreadPool(threads);
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Запускает рабочие потоки, которые обрабатывают команды из очереди.
     */
    public void start() {
        for (int i = 0; i < ((ThreadPoolExecutor) workers).getCorePoolSize(); i++) {
            workers.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Command cmd = queue.take();
                        System.out.println("Команда извлечена: " + cmd.getClass().getSimpleName());
                        cmd.execute();
                        System.out.println("Команда выполнена: " + cmd.getClass().getSimpleName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Поток прерван");
                    } catch (Exception e) {
                        exceptionHandler.handle("EventLoop", e);
                    }
                    if (queue.isEmpty()) {
                        idleBehaviour.run();
                    }
                }
            });
        }
    }

    /**
     * Добавляет команду в очередь на выполнение.
     *
     * @param command команда
     */
    public void submit(Command command) {
        queue.offer(command);
    }

    /**
     * Немедленно завершает все потоки (жёсткая остановка).
     */
    public void stop() {
        workers.shutdownNow();
    }

    /**
     * Плавно завершает все потоки, дожидаясь их завершения.
     *
     * @throws InterruptedException если ожидание было прервано
     */
    public void softStop() throws InterruptedException {
        workers.shutdown();
        workers.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * Устанавливает поведение при пустой очереди.
     * @param behaviour новое поведение
     */
    public void setDefaultBehaviour(Runnable behaviour) {
        this.idleBehaviour = behaviour;
    }

    /**
     * Устанавливает поведение при пустой очереди (то же, что и setDefaultBehaviour).
     */
    public void setBehaviour(Runnable behaviour) {
        this.idleBehaviour = behaviour;
    }

    /**
     * Возвращает текущее поведение при пустой очереди.
     */
    public Runnable getBehaviour() {
        return idleBehaviour;
    }
}
