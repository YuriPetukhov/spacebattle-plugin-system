package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.IUObject;

import java.util.Map;
import java.util.function.Function;

/**
 * Компонент, отвечающий за обработку одной команды от клиента.
 * Использует IoC для получения зависимостей:
 * - репозитория объектов,
 * - фабрики команд,
 * - диспетчера команд.
 *
 * Алгоритм обработки:
 *  1. Найти объект по objectId из DTO.
 *  2. Получить фабрику команды по названию действия.
 *  3. Создать команду и отправить её на выполнение.
 *  4. Вернуть обновлённые свойства объекта.
 */
public class CommandProcessor {

    private final IoC ioc;

    /**
     * Создаёт CommandProcessor с внедрённым IoC-контейнером.
     *
     * @param ioc IoC-контейнер для разрешения зависимостей
     */
    public CommandProcessor(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Выполняет команду, описанную в DTO: находит объект, создаёт команду,
     * отправляет её в диспетчер и возвращает обновлённые свойства объекта.
     *
     * @param dto входная команда с полями objectId, action, params
     * @return карта со статусом и актуальными свойствами объекта
     * @throws Exception если объект не найден или возникают ошибки при выполнении команды
     */
    public Map<String, Object> process(CommandDTO dto) throws Exception {
        // Шаг 1. Получаем объект по ID
        ObjectRepository repository = (ObjectRepository) ioc.resolve("object-repository");
        IUObject target = repository.findById(dto.id())
                .orElseThrow(() -> new ClientInputException("Объект не найден: " + dto.id()));

        // Шаг 2. Получаем фабрику команды по имени действия (action)
        CommandFactory factory = (CommandFactory) ioc.resolve("command:" + dto.action());

        // Шаг 3. Создаём команду и отправляем её в диспетчер
        Command command = factory.create(target, dto);
        @SuppressWarnings("unchecked")
        Function<Command, Void> dispatcher = (Function<Command, Void>) ioc.resolve("command-dispatcher");
        dispatcher.apply(command);

        // Шаг 4. Возвращаем итоговые свойства объекта
        Map<String, Object> properties = target.getAllProperties();

        return Map.of(
                "status", "ok",
                "action", dto.action(),
                "objectId", dto.id(),
                "properties", properties
        );
    }
}
