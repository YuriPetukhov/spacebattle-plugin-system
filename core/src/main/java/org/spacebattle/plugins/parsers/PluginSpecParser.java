package org.spacebattle.plugins.parsers;

import java.util.List;
import java.util.Map;

/**
 * Универсальный интерфейс для парсинга pluginSpec.yaml
 * @param <T> тип объектов, которые возвращает парсер
 */
public interface PluginSpecParser<T> {
    List<T> parse(Map<String, Object> pluginSpec);
}
