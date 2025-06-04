package org.spacebattle.setup;

import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.execution.ExecutionQueue;

/**
 * Конфигуратор, регистрирующий реализацию очереди команд в IoCContainer.
 * <p>
 * В зависимости от параметра запуска {@code -Dloop=execution} используется:
 * <ul>
 *     <li>{@link ExecutionQueue} — однопоточная очередь</li>
 *     <li>{@link EventLoop} — многопоточный EventLoop (по умолчанию)</li>
 * </ul>
 * </p>
 * Выбор делается при запуске сервера. Метод используется один раз в {@code main()}.
 */
public class IoCEventLoopSetup {

    public static void setup(IoCContainer ioc) {
        ExceptionHandler handler = (ExceptionHandler)ioc.resolve("exception-handler", ExceptionHandler.class);

        String loopType = System.getProperty("loop", "event");

        if ("execution".equalsIgnoreCase(loopType)) {
            ExecutionQueue queue = new ExecutionQueue(handler);
            ioc.register("event-loop", (ExecutionQueue) -> queue);
            System.out.println("ExecutionQueue registered (single-threaded mode)");
        } else {
            EventLoop loop = new EventLoop(4, handler);
            loop.start();
            ioc.register("event-loop", (EventLoop) -> loop);
            System.out.println("EventLoop registered and started (multi-threaded mode)");
        }
    }
}

