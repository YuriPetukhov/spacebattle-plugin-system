package org.spacebattle.context;

/**
 * Интерфейс, позволяющий команде предоставить свой контекст (CommandContext).
 */
public interface Contextual {
    CommandContext getContext();
}
