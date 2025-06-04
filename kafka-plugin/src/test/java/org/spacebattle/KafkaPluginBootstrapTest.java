package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaPluginBootstrapTest {

    private IoC ioc;
    private KafkaPluginBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        bootstrap = new KafkaPluginBootstrap();
        bootstrap.setIoC(ioc);
    }

    @Test
    void run_shouldRegisterDependenciesAndStartConsumer() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        when(ioc.contains("JsonToCommand")).thenReturn(false);
        when(ioc.contains("CommandDispatcher")).thenReturn(false);
        EventLoop eventLoop = mock(EventLoop.class);
        when(ioc.resolve("event-loop")).thenReturn(eventLoop);

        try (MockedConstruction<KafkaCommandConsumer> mocked = mockConstruction(KafkaCommandConsumer.class)) {
            bootstrap.run();

            verify(ioc).register(eq("JsonToCommand"), any());
            verify(ioc).register(eq("CommandDispatcher"), any());
            verify(ioc).register(eq("KafkaConsumer"), any());
            verify(ioc).resolve("event-loop");

            assertEquals(1, mocked.constructed().size());
        }
    }


    @Test
    void run_shouldNotRegisterExistingDependencies() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        when(ioc.contains("JsonToCommand")).thenReturn(true);
        when(ioc.contains("CommandDispatcher")).thenReturn(true);
        when(ioc.resolve("event-loop")).thenReturn(mock(EventLoop.class));

        try (MockedConstruction<KafkaCommandConsumer> mocked = mockConstruction(KafkaCommandConsumer.class)) {
            bootstrap.run();

            verify(ioc, never()).register(eq("JsonToCommand"), any());
            verify(ioc, never()).register(eq("CommandDispatcher"), any());
            verify(ioc).register(eq("KafkaConsumer"), any());

            assertEquals(1, mocked.constructed().size());
        }
    }


    @Test
    void run_shouldThrowRuntimeExceptionIfEventLoopNotFound() {
        when(ioc.contains(anyString())).thenReturn(true);
        when(ioc.resolve("event-loop")).thenThrow(new RuntimeException("Loop missing"));

        assertThrows(RuntimeException.class, bootstrap::run);

        verify(ioc).resolve("event-loop");
    }

    @Test
    void run_shouldStartConsumer_whenKafkaEnvIsSet() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        when(ioc.contains(anyString())).thenReturn(false);
        when(ioc.resolve("event-loop")).thenReturn(mock(EventLoop.class));

        try (MockedConstruction<KafkaCommandConsumer> mocked = mockConstruction(KafkaCommandConsumer.class)) {
            bootstrap.run();

            // Проверяем, что consumer зарегистрирован
            verify(ioc).register(eq("KafkaConsumer"), any());

            KafkaCommandConsumer constructed = mocked.constructed().get(0);
            assertNotNull(constructed);
        }
    }

}
