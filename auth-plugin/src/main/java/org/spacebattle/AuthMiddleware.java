package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.context.Contextual;
import org.spacebattle.middleware.Middleware;
import org.spacebattle.context.CommandContext;

/**
 * Middleware, реализующее проверку прав доступа пользователя к объекту перед выполнением команды.
 *
 * <p>Ожидает, что оборачиваемая команда реализует интерфейс {@link Contextual}
 * и содержит в контексте значения {@code userId} и {@code objectId}.
 * Использует {@link AccessRegistry} для проверки разрешения.
 *
 * <p>Если пользователь не имеет доступа к объекту, выбрасывается {@link SecurityException}.
 */
public class AuthMiddleware implements Middleware {

    /**
     * Реестр прав доступа, в котором содержится информация о том, какие пользователи
     * имеют доступ к каким объектам.
     */
    private final AccessRegistry accessRegistry;

    /**
     * Создаёт новое middleware авторизации на основе переданного {@link AccessRegistry}.
     *
     * @param accessRegistry реестр, содержащий информацию о доступе
     */
    public AuthMiddleware(AccessRegistry accessRegistry) {
        this.accessRegistry = accessRegistry;
    }

    /**
     * Оборачивает команду проверкой авторизации:
     * <ul>
     *     <li>Проверяет, что команда реализует {@link Contextual}</li>
     *     <li>Извлекает из контекста {@code userId} и {@code objectId}</li>
     *     <li>Проверяет, разрешён ли доступ пользователю к объекту</li>
     *     <li>Если доступ разрешён — выполняет команду, иначе выбрасывает {@link SecurityException}</li>
     * </ul>
     *
     * @param next команда, подлежащая оборачиванию
     * @return обёрнутая команда с проверкой авторизации
     */
    @Override
    public Command wrap(Command next) {
        return () -> {
            if (!(next instanceof Contextual contextual)) {
                throw new SecurityException("Command is not contextual!");
            }

            CommandContext ctx = contextual.getContext();
            String userId = ctx.get("userId")
                    .map(Object::toString)
                    .orElseThrow(() -> new SecurityException("Missing userId in context"));
            String objectId = ctx.get("objectId")
                    .map(Object::toString)
                    .orElseThrow(() -> new SecurityException("Missing objectId in context"));

            if (!accessRegistry.isAllowed(userId, objectId)) {
                throw new SecurityException("User " + userId + " has no access to object " + objectId);
            }

            next.execute();
        };
    }
}
