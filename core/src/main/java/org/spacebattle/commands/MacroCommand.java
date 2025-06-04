package org.spacebattle.commands;

import java.util.List;

/**
 * Макрокоманда — агрегирует несколько команд и выполняет их последовательно.
 */
public class MacroCommand implements Command {

    private final List<Command> commands;

    /**
     * Создаёт макрокоманду из списка команд.
     *
     * @param commands команды, которые нужно выполнить последовательно
     */
    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    /**
     * Последовательно выполняет все вложенные команды.
     *
     * @throws Exception если любая из команд выбрасывает исключение
     */
    @Override
    public void execute() throws Exception {
        for (Command command : commands) {
            command.execute();
        }
    }
}
