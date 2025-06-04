package org.spacebattle;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Публикует события игры в Kafka-топики.
 * <p>
 * Используется для отправки сериализованных сообщений (например, JSON) в заданные Kafka-темы.
 * </p>
 */
public class KafkaEventPublisher {

    private final KafkaProducer<String, String> producer;

    /**
     * Создаёт Kafka-публикатор с настройками по умолчанию.
     * Адрес брокера: {@code localhost:9092}.
     */
    public KafkaEventPublisher() {

        String bootstrapServers = System.getProperty("KAFKA_BOOTSTRAP_SERVERS");

        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new RuntimeException("KAFKA_BOOTSTRAP_SERVERS is not set");
        }
        System.out.println("KafkaEventPublisher bootstrap.servers = " + bootstrapServers);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    /**
     * Публикует сообщение в указанный Kafka-топик.
     *
     * @param topic   название Kafka-топика
     * @param message сообщение в виде строки (обычно JSON)
     */
    public void publish(String topic, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record, (meta, err) -> {
            if (err != null) {
                System.err.println("Kafka send error: " + err.getMessage());
            } else {
                System.out.println("Kafka event sent: " + message);
            }
        });
    }

    /**
     * Закрывает Kafka-проюсер и освобождает ресурсы.
     */
    public void close() {
        producer.close();
    }
}
