package org.spacebattle.dsl;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для выполнения команд, определённых в формате DSL.
 * <p>
 * Используется для обработки списка команд, описанных в YAML/JSON-формате как список Map.
 * Каждая команда должна быть совместима с форматом CommandDTO.
 */
public interface DslCommandExecutor {
    /**
     * Выполняет список команд, представленных в формате DSL.
     *
     * @param rawCommands список команд в виде map-представлений (например, загруженных из YAML)
     */
    void executeDslCommands(List<Map<String, Object>> rawCommands);
}
