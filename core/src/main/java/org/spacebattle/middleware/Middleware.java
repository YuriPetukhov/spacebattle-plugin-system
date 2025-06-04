package org.spacebattle.middleware;

import org.spacebattle.commands.Command;

/**
 * Middleware — фильтр/обёртка над командой, который может выполнять действия до/после команды.
 */
public interface Middleware {
    Command wrap(Command next);
}
