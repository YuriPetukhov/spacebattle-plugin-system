package org.spacebattle.commands;

/**
 * Базовый интерфейс команды, поддерживающий шаблон Command.
 * Команда представляет собой действие, которое можно выполнить.
 */
public interface Command {
    /**
     * Выполняет команду.
     *
     * @throws Exception если в процессе выполнения возникает ошибка
     */
    void execute() throws Exception;
}

