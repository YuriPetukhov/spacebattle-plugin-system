package org.spacebattle.dsl;

import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.uobject.IUObject;

/**
 * Интерфейс фабрики команд, применяемой в контексте DSL-обработчиков.
 * <p>
 * Используется для динамической генерации и исполнения команд,
 * описанных в DSL-формате, таких как YAML или JSON-файлы.
 * </p>
 * <p>
 * Значение метода {@link #getActionName()} по умолчанию — "dsl-runner".
 */
public interface DSLCommandFactory extends CommandFactory {

    /**
     * Возвращает имя действия, связанного с DSL-командами.
     * По умолчанию — "dsl-runner".
     */
    @Override
    default String getActionName() {
        return "dsl-runner";
    }

    /**
     * Создаёт команду на основе объекта и DTO, полученных из DSL.
     *
     * @param target объект, к которому применяется команда
     * @param dto    параметры команды, полученные из DSL
     * @return команда, готовая к исполнению
     */
    @Override
    Command create(IUObject target, CommandDTO dto);
}
