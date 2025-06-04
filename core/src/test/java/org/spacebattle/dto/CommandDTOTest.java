package org.spacebattle.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CommandDTOTest {

    // Тесты для основного конструктора
    @Test
    void constructor_ShouldCreateValidObject_WhenAllFieldsProvided() {
        Map<String, Object> params = Map.of("speed", 10);
        CommandDTO dto = new CommandDTO("ship1", "move", params);

        assertEquals("ship1", dto.id());
        assertEquals("move", dto.action());
        assertEquals(10, dto.params().get("speed"));
    }

    @Test
    void constructor_ShouldUseEmptyParams_WhenNullProvided() {
        CommandDTO dto = new CommandDTO("ship1", "move", null);
        assertTrue(dto.params().isEmpty());
    }

    // Тесты для fromMap()
    @Test
    void fromMap_ShouldCreateValidDTO_WithRequiredFields() {
        Map<String, Object> input = Map.of(
                "action", "fire",
                "id", "player1",
                "params", Map.of("weapon", "laser")
        );

        CommandDTO dto = CommandDTO.fromMap(input);

        assertEquals("player1", dto.id());
        assertEquals("fire", dto.action());
        assertEquals("laser", dto.params().get("weapon"));
    }

    @Test
    void fromMap_ShouldUseAnonId_WhenIdMissing() {
        Map<String, Object> input = Map.of(
                "action", "rotate"
        );

        CommandDTO dto = CommandDTO.fromMap(input);
        assertEquals("anon", dto.id());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void fromMap_ShouldThrow_WhenActionMissingOrEmpty(Map<String, Object> input) {
        assertThrows(IllegalArgumentException.class, () -> CommandDTO.fromMap(input));
    }

    @Test
    void fromMap_ShouldHandleNullParams() {
        Map<String, Object> input = new HashMap<>();
        input.put("action", "stop");
        input.put("params", null);

        CommandDTO dto = CommandDTO.fromMap(input);
        assertTrue(dto.params().isEmpty());
    }

    @Test
    void fromMap_ShouldFilterNonStringParamKeys() {
        Map<Object, Object> rawParams = new HashMap<>();
        rawParams.put("valid", 100);
        rawParams.put(42, "invalid");

        Map<String, Object> input = Map.of(
                "action", "jump",
                "params", rawParams
        );

        CommandDTO dto = CommandDTO.fromMap(input);
        assertEquals(100, dto.params().get("valid"));
        assertFalse(dto.params().containsKey(42));
    }

    // Тесты на иммутабельность
    @Test
    void paramsMap_ShouldBeImmutable_AfterCreation() {
        Map<String, Object> mutableParams = new HashMap<>();
        mutableParams.put("temp", 1);

        CommandDTO dto = new CommandDTO("test", "action", mutableParams);
        mutableParams.put("temp", 2); // Изменение оригинала не должно влиять на DTO

        assertEquals(1, dto.params().get("temp"));
        assertThrows(UnsupportedOperationException.class, () -> {
            dto.params().put("new", 3);
        });
    }

    @Test
    void fromMap_ShouldReturnImmutableParams() {
        Map<String, Object> input = Map.of(
                "action", "shield",
                "params", Map.of("power", 75)
        );

        CommandDTO dto = CommandDTO.fromMap(input);
        assertThrows(UnsupportedOperationException.class, () -> {
            dto.params().put("hacked", 100);
        });
    }

    // Параметризованные тесты для разных типов параметров
    static Stream<Map<String, Object>> paramTypesProvider() {
        return Stream.of(
                Map.of("int", 42),
                Map.of("double", 3.14),
                Map.of("string", "value"),
                Map.of("boolean", true),
                Map.of("nested", Map.of("key", "value"))
        );
    }

    @ParameterizedTest
    @MethodSource("paramTypesProvider")
    void fromMap_ShouldPreserveParamTypes(Map<String, Object> params) {
        Map<String, Object> input = new HashMap<>();
        input.put("action", "test");
        input.put("params", params);

        CommandDTO dto = CommandDTO.fromMap(input);
        assertEquals(params, dto.params());
    }

    // Тест на обработку case-sensitive ключей
    @Test
    void fromMap_ShouldPreserveKeyCase() {
        Map<String, Object> input = Map.of(
                "ACTION", "lower", // Должно игнорироваться
                "action", "actual",
                "params", Map.of("CamelCase", "value")
        );

        CommandDTO dto = CommandDTO.fromMap(input);
        assertEquals("actual", dto.action());
        assertEquals("value", dto.params().get("CamelCase"));
    }
}