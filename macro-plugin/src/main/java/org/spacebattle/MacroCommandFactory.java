package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.commands.MacroCommand;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Фабрика для создания {@link MacroCommand}, которая объединяет несколько вложенных команд.
 * <p>
 * Ожидает в DTO список команд в параметре "commands".
 * </p>
 *
 * Пример DTO:
 * <pre>
 * {
 *   "action": "macro",
 *   "params": {
 *     "commands": [
 *       { "action": "move", "params": {...} },
 *       { "action": "rotate", "params": {...} }
 *     ]
 *   }
 * }
 * </pre>
 */
public class MacroCommandFactory implements CommandFactory {

    private IoC ioc;

    /**
     * Конструктор с внедрением {@link IoC}-контейнера.
     *
     * @param ioc IoC-контейнер, используемый для получения вложенных {@link CommandFactory}
     */
    public MacroCommandFactory(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Конструктор без параметров (можно вызвать setIoC позже).
     */
    public MacroCommandFactory() {}

    /**
     * Устанавливает IoC-контейнер.
     *
     * @param ioc IoC-контейнер
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Возвращает имя действия — "macro".
     *
     * @return строка "macro"
     */
    @Override
    public String getActionName() {
        return "macro";
    }

    /**
     * Создаёт {@link MacroCommand} на основе списка вложенных команд в DTO.
     *
     * @param target объект, к которому применяются все вложенные команды
     * @param dto    объект DTO, содержащий список подкоманд в параметре "commands"
     * @return макрокоманда
     * @throws RuntimeException если одна из вложенных команд не может быть разрешена
     */
    @Override
    public Command create(IUObject target, CommandDTO dto) {
        List<Map<String, Object>> subcommandsRaw = (List<Map<String, Object>>) dto.params().get("commands");
        List<Command> commands = new ArrayList<>();

        for (Map<String, Object> cmdMap : subcommandsRaw) {
            CommandDTO subDTO = CommandDTO.fromMap(cmdMap);
            String key = "command:" + subDTO.action();

            Object resolved = ioc.resolve(key);
            if (!(resolved instanceof CommandFactory factory)) {
                throw new RuntimeException("Invalid command in macro: " + subDTO.action());
            }

            Command command = factory.create(target, subDTO);
            commands.add(command);
        }

        return new MacroCommand(commands);
    }
}
