package org.spacebattle.commands;

import org.spacebattle.uobject.IUObject;
import org.spacebattle.dto.CommandDTO;

/**
 * Фабрика команд — создает команды по имени действия и параметрам.
 * Используется для построения команд из DTO (например, полученных по сети или из плагина).
 */
public interface CommandFactory {
    /**
     * Возвращает имя действия, которое эта фабрика обслуживает (например, "move").
     * Используется для выбора нужной фабрики в IoC-контейнере.
     */
    String getActionName();

    /**
     * Создаёт команду для указанного объекта на основе переданного DTO.
     *
     * @param target объект, к которому применяется команда
     * @param dto параметры команды (например, из пользовательского ввода)
     * @return объект команды, готовый к выполнению
     */
    Command create(IUObject target, CommandDTO dto);
}
