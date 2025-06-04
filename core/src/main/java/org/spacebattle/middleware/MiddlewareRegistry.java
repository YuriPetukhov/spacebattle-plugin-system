package org.spacebattle.middleware;

import org.spacebattle.commands.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище middleware-компонентов.
 * Позволяет регистрировать цепочку middleware и применять их к команде.
 *
 * Принцип работы:
 * - Middleware добавляются в порядке регистрации (register)
 * - При вызове wrap(command) они оборачивают команду в обратном порядке
 *   (последний зарегистрированный middleware — самый внешний)
 */
public class MiddlewareRegistry {
    private final List<Middleware> chain = new ArrayList<>();

    /**
     * Регистрирует middleware, добавляя его в цепочку.
     *
     * @param middleware реализация Middleware
     */
    public void register(Middleware middleware) {
        chain.add(middleware);
    }

    /**
     * Оборачивает команду всеми зарегистрированными middleware.
     * Выполняется в обратном порядке, чтобы порядок был вложенным.
     *
     * @param baseCommand команда, которую нужно обернуть
     * @return команда, обёрнутая всеми middleware
     */
    public Command wrap(Command baseCommand) {
        Command wrapped = baseCommand;
        for (int i = chain.size() - 1; i >= 0; i--) {
            wrapped = chain.get(i).wrap(wrapped);
        }
        return wrapped;
    }
}
