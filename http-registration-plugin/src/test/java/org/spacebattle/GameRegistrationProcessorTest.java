package org.spacebattle;

import org.junit.jupiter.api.*;
import org.spacebattle.dsl.DslLoader;
import org.spacebattle.dsl.DslSource;
import org.spacebattle.ioc.IoC;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameRegistrationProcessorTest {

    private IoC ioc;
    private DslLoader<Void> loader;
    private GameRegistrationProcessor processor;

    private final File testDir = new File("objects");

    @BeforeEach
    void setUp() throws Exception {
        ioc = mock(IoC.class);
        loader = mock(DslLoader.class);
        when(ioc.resolve("dsl-object-loader")).thenReturn(loader);
        processor = new GameRegistrationProcessor(ioc);

        // Подготовка папки и одного тестового файла
        if (!testDir.exists()) testDir.mkdir();
        createTestYaml("Alice.yaml");
    }

    @AfterEach
    void tearDown() {
        // Очистка папки objects
        for (File file : testDir.listFiles()) {
            file.delete();
        }
        testDir.delete();
    }

    void createTestYaml(String filename) throws Exception {
        File file = new File(testDir, filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("objects:\n  dummy:\n    properties: {}\n    capabilities: []\n");
        }
    }

    @Test
    void register_shouldThrowIfPlayersListIsMissing() {
        Map<String, Object> input = Map.of();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> processor.register(input));
        assertEquals("Поле 'players' не должно быть пустым", ex.getMessage());
    }

    @Test
    void register_shouldSkipMissingFilesAndStillReturnSuccess() throws Exception {
        Map<String, Object> input = Map.of("players", List.of("Alice", "Bob")); // Alice есть, Bob нет

        Map<String, Object> result = processor.register(input);

        assertEquals("ok", result.get("status"));
        assertTrue(result.containsKey("token"));
        assertEquals(List.of("Alice", "Bob"), result.get("registered"));

        // Проверяем, что loader был вызван только один раз
        verify(loader, times(1)).load(any(DslSource.class));
    }

    @Test
    void register_shouldLoadAllExistingFiles() throws Exception {
        createTestYaml("Bob.yaml");

        Map<String, Object> input = Map.of("players", List.of("Alice", "Bob"));

        Map<String, Object> result = processor.register(input);

        assertEquals("ok", result.get("status"));
        assertEquals(List.of("Alice", "Bob"), result.get("registered"));
        assertTrue(result.get("token") instanceof String);

        verify(loader, times(2)).load(any(DslSource.class));
    }

    @Test
    void register_shouldPropagateLoaderException() throws Exception {
        doThrow(new RuntimeException("DSL load error")).when(loader).load(any(DslSource.class));

        Map<String, Object> input = Map.of("players", List.of("Alice"));

        Exception ex = assertThrows(RuntimeException.class, () -> processor.register(input));
        assertEquals("DSL load error", ex.getMessage());
    }
}
