package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.execution.EventLoop;

/**
 * Команда для немедленной остановки {@link EventLoop}.
 * Вызывает метод {@code stop()}, который завершает работу планировщика.
 */
public class HardStopCommand implements Command {

    private final EventLoop eventLoop;

    /**
     * Создаёт команду, ассоциированную с конкретным {@link EventLoop}.
     *
     * @param eventLoop объект планировщика, который необходимо остановить
     */
    public HardStopCommand(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    /**
     * Выполняет остановку {@link EventLoop} вызовом метода {@code stop()}.
     */
    @Override
    public void execute() {
        eventLoop.stop();
    }
}
