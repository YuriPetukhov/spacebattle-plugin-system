package org.spacebattle.plugins.parsers;

import org.spacebattle.plugins.parsers.models.ExceptionHandlerSpec;

import java.util.*;

/**
 * Парсер секции "exceptionHandlers" или "exceptions" из спецификации плагина.
 * <p>
 * Ожидаемый формат:
 * plugin:
 *   exceptionHandlers:
 *     - class: org.spacebattle.MyExceptionHandler
 * или
 * plugin:
 *   exceptions:
 *     - class: org.spacebattle.MyExceptionHandler
 */
public class ExceptionHandlerSpecParser implements PluginSpecParser<ExceptionHandlerSpec> {

    /**
     * Извлекает список {@link ExceptionHandlerSpec} из спецификации плагина.
     *
     * @param pluginSpec YAML/JSON-карта, содержащая структуру плагина
     * @return список спецификаций обработчиков исключений
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ExceptionHandlerSpec> parse(Map<String, Object> pluginSpec) {
        if (pluginSpec == null) return Collections.emptyList();

        Object inner = pluginSpec.get("plugin");
        if (!(inner instanceof Map)) return Collections.emptyList();

        Map<String, Object> plugin = (Map<String, Object>) inner;

        // Пытаемся получить список из "exceptionHandlers" или "exceptions"
        Object handlersRaw = plugin.get("exceptionHandlers");
        if (handlersRaw == null) {
            handlersRaw = plugin.get("exceptions");
        }

        if (!(handlersRaw instanceof List<?> rawList)) {
            return Collections.emptyList();
        }

        List<ExceptionHandlerSpec> result = new ArrayList<>();
        for (Object item : rawList) {
            if (item instanceof Map<?, ?> map) {
                Object className = map.get("class");
                if (className instanceof String name) {
                    result.add(new ExceptionHandlerSpec(name));
                }
            }
        }

        return result;
    }
}
