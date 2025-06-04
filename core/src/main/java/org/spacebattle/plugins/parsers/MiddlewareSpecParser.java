package org.spacebattle.plugins.parsers;

import org.spacebattle.plugins.parsers.models.MiddlewareSpec;

import java.util.*;

/**
 * Парсер списка middleware из спецификации плагина.
 * <p>
 * Поддерживаются два формата:
 * <ul>
 *   <li>Строка: <pre>"org.example.MyMiddleware"</pre></li>
 *   <li>Объект с ключом <code>class</code>: <pre>{ class: "org.example.MyMiddleware" }</pre></li>
 * </ul>
 * Пример:
 * <pre>
 * plugin:
 *   middleware:
 *     - "org.example.Simple"
 *     - { class: "org.example.Advanced" }
 * </pre>
 */
public class MiddlewareSpecParser implements PluginSpecParser<MiddlewareSpec> {

    /**
     * Извлекает список {@link MiddlewareSpec} из DSL-представления плагина.
     *
     * @param pluginSpec карта с описанием плагина (обычно загруженная из YAML)
     * @return список middleware
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MiddlewareSpec> parse(Map<String, Object> pluginSpec) {
        Map<String, Object> plugin = (Map<String, Object>) pluginSpec.get("plugin");
        if (plugin == null || !plugin.containsKey("middleware")) return Collections.emptyList();

        List<Object> rawList = (List<Object>) plugin.get("middleware");
        List<MiddlewareSpec> result = new ArrayList<>();

        for (Object mw : rawList) {
            if (mw instanceof String str) {
                result.add(new MiddlewareSpec(str));
            } else if (mw instanceof Map<?, ?> map && map.containsKey("class")) {
                result.add(new MiddlewareSpec(map.get("class").toString()));
            } else {
                throw new RuntimeException("Invalid middleware format: " + mw);
            }
        }

        return result;
    }
}
