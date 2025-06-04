package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandlerResolver;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.factory.ObjectFactory;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaCommandConsumer implements Runnable {

    private final EventLoop eventLoop;
    private final IoC ioc;
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaCommandConsumer(EventLoop eventLoop, IoC ioc) {
        this(eventLoop, ioc, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
    }

    // Конструктор для тестирования
    KafkaCommandConsumer(EventLoop eventLoop, IoC ioc, String bootstrapServers) {
        this.eventLoop = eventLoop;
        this.ioc = ioc;

        validateBootstrapServers(bootstrapServers);
        this.consumer = createKafkaConsumer(bootstrapServers);
        this.consumer.subscribe(Collections.singletonList("commands"));
    }

    private void validateBootstrapServers(String bootstrapServers) {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new RuntimeException("KAFKA_BOOTSTRAP_SERVERS is not set");
        }
    }

    KafkaConsumer<String, String> createKafkaConsumer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "spacebattle-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                }
            } catch (Exception e) {
                handlePollError(e);
            }
        }
    }

    void processRecord(ConsumerRecord<String, String> record) {
        try {
            CommandDTO dto = objectMapper.readValue(record.value(), CommandDTO.class);
            Command command = createCommand(dto);
            eventLoop.submit(command);
        } catch (Exception e) {
            handleProcessingError(record, e);
        }
    }

    Command createCommand(CommandDTO dto) {
        CommandFactory commandFactory = (CommandFactory) ioc.resolve("command:" + dto.action(), CommandFactory.class);
        ObjectFactory objectFactory = (ObjectFactory) ioc.resolve("object-factory", ObjectFactory.class);
        IUObject target = objectFactory.getOrCreate(dto.id());
        return commandFactory.create(target, dto);
    }

    void handleProcessingError(ConsumerRecord<String, String> record, Exception e) {
        ExceptionHandlerResolver resolver = (ExceptionHandlerResolver) ioc.resolve("exception-handler-resolver", ExceptionHandlerResolver.class);
        ExceptionHandler handler = resolver.resolve("kafka:" + record.key());
        handler.handle("KafkaConsumer", e);
    }

    void handlePollError(Exception e) {
        System.err.println("KafkaConsumer poll error: " + e.getMessage());
        e.printStackTrace();
    }
}