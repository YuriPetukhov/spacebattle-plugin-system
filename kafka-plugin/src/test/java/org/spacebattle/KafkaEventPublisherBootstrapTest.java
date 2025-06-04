package org.spacebattle;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.spacebattle.ioc.IoCContainer;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaEventPublisherBootstrapTest {

    private KafkaEventPublisherBootstrap bootstrap;
    private IoCContainer ioc;

    @BeforeEach
    void setUp() {
        bootstrap = new KafkaEventPublisherBootstrap();
        ioc = mock(IoCContainer.class);
        bootstrap.setIoC(ioc);
    }

    @Test
    void run_shouldRegisterPublisher_whenEnvVarIsSet() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        try (MockedConstruction<KafkaProducer> mocked = mockConstruction(KafkaProducer.class)) {
            bootstrap.run();

            KafkaProducer<String, String> mockProducer = mocked.constructed().get(0);

            verify(ioc).register(eq("KafkaEventPublisher"), any());
            verifyNoMoreInteractions(ioc);
        }
    }


    @Test
    void run_shouldThrowException_whenEnvVarNotSet() {
        System.clearProperty("KAFKA_BOOTSTRAP_SERVERS");
        assertThrows(RuntimeException.class, bootstrap::run);
    }
}
