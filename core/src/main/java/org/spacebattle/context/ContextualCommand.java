package org.spacebattle.context;

import lombok.Getter;
import org.spacebattle.commands.Command;

/**
 * Обёртка для команды, содержащая также объект контекста {@link CommandContext}.
 * Используется для добавления метаинформации (например, токена или имени пользователя) к команде.
 *
 * Реализует интерфейсы:
 * - {@link Command} — делегирует выполнение вложенной команды.
 * - {@link Contextual} — предоставляет доступ к связанному контексту.
 */
@Getter
public class ContextualCommand implements Command, Contextual {

    private final Command inner;
    private final CommandContext context;

    /**
     * Создаёт новую обёртку команды с указанным контекстом.
     *
     * @param inner   команда, которую следует обернуть
     * @param context контекст, содержащий метаданные
     */
    public ContextualCommand(Command inner, CommandContext context) {
        this.inner = inner;
        this.context = context;
    }

    /**
     * Выполняет вложенную команду.
     *
     * @throws Exception если выполнение завершилось с ошибкой
     */
    @Override
    public void execute() throws Exception {
        inner.execute();
    }

    /**
     * Возвращает связанный контекст команды.
     *
     * @return объект {@link CommandContext}
     */
    @Override
    public CommandContext getContext() {
        return context;
    }

}
