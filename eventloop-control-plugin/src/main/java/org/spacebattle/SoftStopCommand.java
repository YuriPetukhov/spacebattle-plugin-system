package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.execution.EventLoop;

/**
 * Команда мягкой остановки {@link EventLoop}.
 * Вместо немедленной остановки устанавливает новое поведение, которое
 * проверяет очередь команд и завершает работу только когда она пуста.
 */
public class SoftStopCommand implements Command {

    private final EventLoop eventLoop;

    /**
     * Создаёт команду для мягкой остановки планировщика.
     *
     * @param eventLoop объект {@link EventLoop}, поведение которого будет изменено
     */
    public SoftStopCommand(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    /**
     * Изменяет поведение {@link EventLoop}, чтобы он останавливался
     * только после выполнения всех команд в очереди.
     */
    @Override
    public void execute() {
        Runnable oldBehaviour = eventLoop.getBehaviour();

        eventLoop.setBehaviour(() -> {
            if (!eventLoop.getQueue().isEmpty()) {
                oldBehaviour.run();
            } else {
                eventLoop.stop();
            }
        });
    }
}
