package org.spacebattle;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.spacebattle.ioc.IoC;
import org.spacebattle.ioc.IoCContainer;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * Класс инициализации Kafka-публикатора событий.
 * Подключается к Kafka-серверу, создает продюсера и регистрирует его в IoC-контейнере как Consumer<String>.
 * Используется для отправки игровых событий в топик "events".
 */
public class KafkaEventPublisherBootstrap {

    private IoCContainer ioc;

    /**
     * Устанавливает IoC-контейнер.
     *
     * @param ioc контейнер зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = (IoCContainer) ioc;
    }

    /**
     * Метод инициализации Kafka-публикатора.
     * Использует переменную окружения KAFKA_BOOTSTRAP_SERVERS для настройки.
     * Регистрирует обработчик событий как "KafkaEventPublisher" в IoC.
     */
    public void run() {
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        if ((bootstrapServers == null || bootstrapServers.isBlank()) &&
                (bootstrapServers = System.getProperty("KAFKA_BOOTSTRAP_SERVERS")) == null) {
            throw new RuntimeException("KAFKA_BOOTSTRAP_SERVERS is not set");
        }


        System.out.println("KafkaEventPublisherBootstrap bootstrap.servers = " + bootstrapServers);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Объект, публикующий строковые события в Kafka
        Consumer<String> eventPublisher = event -> {
            ProducerRecord<String, String> record = new ProducerRecord<>("events", null, event);
            producer.send(record);
            System.out.println("Kafka event published: " + event);
        };

        // Регистрируем обработчик в IoC
        ioc.register("KafkaEventPublisher", args -> eventPublisher);

        System.out.println("Kafka event publisher initialized.");
    }
}
