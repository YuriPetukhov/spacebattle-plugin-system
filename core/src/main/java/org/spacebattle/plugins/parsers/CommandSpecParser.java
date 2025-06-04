package org.spacebattle.plugins.parsers;

import org.spacebattle.plugins.parsers.models.CommandSpec;

import java.util.*;

/**
 * Парсер секции "commands" из спецификации плагина.
 * <p>
 * Ожидаемый формат:
 *
 * plugin:
 *   commands:
 *     - name: move
 *       handler: org.spacebattle.commands.MoveCommand
 *       type: command
 *
 * Если поле "type" отсутствует, по умолчанию используется "command".
 */
public class CommandSpecParser implements PluginSpecParser<CommandSpec> {

    /**
     * Парсит список команд из спецификации плагина.
     *
     * @param pluginSpec JSON/YAML-спецификация плагина
     * @return список объектов {@link CommandSpec}
     */
    @SuppressWarnings("unchecked")
    public List<CommandSpec> parse(Map<String, Object> pluginSpec) {
        Map<String, Object> plugin = (Map<String, Object>) pluginSpec.get("plugin");
        if (plugin == null || !plugin.containsKey("commands")) return Collections.emptyList();

        List<Map<String, String>> commands = (List<Map<String, String>>) plugin.get("commands");
        if (commands == null) return Collections.emptyList();

        List<CommandSpec> result = new ArrayList<>();
        for (Map<String, String> cmd : commands) {
            String name = cmd.get("name");
            String className = cmd.get("handler");
            String type = cmd.getOrDefault("type", "command");
            result.add(new CommandSpec(name, className, type));
        }

        return result;
    }
}
