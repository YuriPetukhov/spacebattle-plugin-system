package org.spacebattle;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class KafkaEventPublisherTest {

    @BeforeEach
    void clearEnv() {
        System.clearProperty("KAFKA_BOOTSTRAP_SERVERS");
    }

    @Test
    void constructor_shouldThrowIfEnvMissing() {
        assertThrows(RuntimeException.class, KafkaEventPublisher::new);
    }

    @Test
    void constructor_shouldUseEnvVar() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        try (MockedConstruction<KafkaProducer> mocked = mockConstruction(KafkaProducer.class)) {
            KafkaEventPublisher publisher = new KafkaEventPublisher();
            publisher.close(); // Закрытие ресурса
            assert mocked.constructed().size() == 1;
        }
    }

    @Test
    void publish_shouldSendMessage() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        try (MockedConstruction<KafkaProducer> mocked = mockConstruction(KafkaProducer.class,
                (mock, context) -> {
                    when(mock.send(any(ProducerRecord.class), any())).thenReturn(mock(Future.class));
                })) {
            KafkaEventPublisher publisher = new KafkaEventPublisher();
            publisher.publish("events", "test-message");

            KafkaProducer<String, String> producerMock = mocked.constructed().get(0);
            verify(producerMock).send(any(ProducerRecord.class), any());
            publisher.close();
        }
    }

    @Test
    void close_shouldCloseProducer() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        try (MockedConstruction<KafkaProducer> mocked = mockConstruction(KafkaProducer.class)) {
            KafkaEventPublisher publisher = new KafkaEventPublisher();
            KafkaProducer<String, String> producerMock = mocked.constructed().get(0);

            publisher.close();

            verify(producerMock).close();
        }
    }
}
