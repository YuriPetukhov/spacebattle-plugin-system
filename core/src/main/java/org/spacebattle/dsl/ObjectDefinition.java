package org.spacebattle.dsl;

import java.util.List;
import java.util.Map;

/**
 * Представляет объект, описанный в YAML DSL.
 * Пример YAML:
 * objects:
 *   ship-1:
 *     properties:
 *       location: { x: 0, y: 0 }
 *       velocity: { dx: 1, dy: 1 }
 *     capabilities:
 *       - move
 *       - rotate
 */
public class ObjectDefinition {
    /**
     * Карта свойств (ключ — имя свойства, значение — произвольное значение).
     */
    public Map<String, Object> properties;

    /**
     * Список способностей (например, move, rotate).
     */
    public List<String> capabilities;
}
