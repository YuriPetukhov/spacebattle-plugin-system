package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

import java.io.File;
import java.util.List;
import java.util.function.Function;

/**
 * Команда, загружающая команды из DSL-файла и исполняющая их.
 * Формат DSL описан в виде массива объектов в файле dsl/commands.json,
 * где каждый элемент представляет собой CommandDTO.
 */
public class JsonDSLCommandRunner implements Command {

    private final IoC container;

    /**
     * Конструктор принимает IoC-контейнер, из которого будут извлекаться зависимости.
     * @param container IoC-контейнер
     */
    public JsonDSLCommandRunner(IoC container) {
        this.container = container;
    }

    /**
     * Выполняет команды из DSL-файла. Для каждой команды:
     * - ищет объект-цель по id, либо создаёт новый DefaultUObject
     * - извлекает CommandFactory по имени действия
     * - создаёт и отправляет команду в EventLoop
     *
     * Если файл не найден — выполнение пропускается.
     */
    @Override
    public void execute() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("commands.json");
        if (!file.exists()) {
            System.err.println("Файл commands.json не найден.");
            return;
        }

        List<CommandDTO> commands = List.of(mapper.readValue(file, CommandDTO[].class));

        EventLoop eventLoop = (EventLoop) container.resolve("event-loop");
        for (CommandDTO dto : commands) {
            String key = "object:" + dto.id();
            IUObject target;
            try {
                target = (IUObject) container.resolve(key);
            } catch (Exception e) {
                target = new DefaultUObject();
                IUObject finalTarget = target;
                container.resolve("IoC.Register", key, (Function<Object[], Object>) args -> finalTarget);
                System.out.println("DSL зарегистрировал новый объект: " + dto.id());
            }

            CommandFactory factory = (CommandFactory) container.resolve("command:" + dto.action());
            Command cmd = factory.create(target, dto);
            eventLoop.submit(cmd);
        }
    }
}
