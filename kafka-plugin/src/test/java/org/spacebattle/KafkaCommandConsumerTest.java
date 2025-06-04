package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandlerResolver;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.factory.ObjectFactory;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaCommandConsumerTest {

    @Mock
    private EventLoop eventLoop;
    @Mock
    private IoC ioc;
    @Mock
    private KafkaConsumer<String, String> kafkaConsumer;
    @Mock
    private CommandFactory commandFactory;
    @Mock
    private ObjectFactory objectFactory;
    @Mock
    private IUObject target;
    @Mock
    private Command command;
    @Mock
    private ExceptionHandlerResolver exceptionHandlerResolver;
    @Mock
    private ExceptionHandler exceptionHandler;

    private KafkaCommandConsumer createTestConsumer() {
        return new KafkaCommandConsumer(eventLoop, ioc, "test-server:9092") {
            @Override
            KafkaConsumer<String, String> createKafkaConsumer(String bootstrapServers) {
                return kafkaConsumer;
            }
        };
    }

    @Test
    void shouldSubscribeToCommandsTopic() {
        KafkaCommandConsumer consumer = createTestConsumer();
        verify(kafkaConsumer).subscribe(Collections.singletonList("commands"));
    }

    @Test
    void shouldThrowWhenBootstrapServersNotSet() {
        assertThrows(RuntimeException.class, () ->
                new KafkaCommandConsumer(eventLoop, ioc, null));
    }

    @Test
    void handleProcessingError_shouldInvokeExceptionHandler() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("commands", 0, 0, "key", "invalid");
        ExceptionHandler handler = mock(ExceptionHandler.class);
        ExceptionHandlerResolver resolver = mock(ExceptionHandlerResolver.class);
        Exception ex = new RuntimeException("fail");

        when(ioc.resolve("exception-handler-resolver", ExceptionHandlerResolver.class)).thenReturn(resolver);
        when(resolver.resolve("kafka:key")).thenReturn(handler);

        KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc, "localhost:9092");
        consumer.handleProcessingError(record, ex);

        verify(handler).handle("KafkaConsumer", ex);
    }
    @Test
    void handlePollError_shouldPrintStackTrace() {
        KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc, "localhost:9092");
        Exception e = new RuntimeException("poll failed");
        consumer.handlePollError(e);
    }

    @Test
    void createKafkaConsumer_shouldReturnConfiguredConsumer() {
        KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc, "localhost:9092");
        KafkaConsumer<String, String> kafka = consumer.createKafkaConsumer("localhost:9092");
        assertNotNull(kafka);
    }

    @Test
    void processRecord_shouldSubmitCommand() throws Exception {
        KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc, "localhost:9092");
        CommandDTO dto = new CommandDTO("ship-1", "move", Map.of());

        String json = new ObjectMapper().writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("commands", 0, 0, "user", json);

        CommandFactory factory = mock(CommandFactory.class);
        ObjectFactory objectFactory = mock(ObjectFactory.class);
        IUObject target = mock(IUObject.class);
        Command command = mock(Command.class);

        ExceptionHandler handler = mock(ExceptionHandler.class);
        ExceptionHandlerResolver resolver = mock(ExceptionHandlerResolver.class);
        lenient().when(resolver.resolve("kafka:user")).thenReturn(handler);
        lenient().when(ioc.resolve("exception-handler-resolver", ExceptionHandlerResolver.class)).thenReturn(resolver);

        when(ioc.resolve("command:move", CommandFactory.class)).thenReturn(factory);
        lenient().when(ioc.resolve("object-factory", ObjectFactory.class)).thenReturn(objectFactory);

        when(objectFactory.getOrCreate("ship-1")).thenReturn(target);
        when(factory.create(target, dto)).thenReturn(command);

        consumer.processRecord(record);

        verify(eventLoop).submit(command);
    }

    @Test
    void createCommand_shouldBuildCommandCorrectly() {
        CommandDTO dto = new CommandDTO( "ship-1", "move", Map.of("dx", 1));
        CommandFactory factory = mock(CommandFactory.class);
        ObjectFactory objectFactory = mock(ObjectFactory.class);
        IUObject target = mock(IUObject.class);
        Command command = mock(Command.class);

        when(ioc.resolve("command:move", CommandFactory.class)).thenReturn(factory);
        when(ioc.resolve("object-factory", ObjectFactory.class)).thenReturn(objectFactory);
        when(objectFactory.getOrCreate("ship-1")).thenReturn(target);
        when(factory.create(target, dto)).thenReturn(command);

        KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc, "localhost:9092");
        Command result = consumer.createCommand(dto);

        assertSame(command, result);
    }

}