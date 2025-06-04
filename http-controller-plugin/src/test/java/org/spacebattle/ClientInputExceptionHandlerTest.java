package org.spacebattle;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientInputExceptionHandlerTest {

    @Test
    void handle_shouldLogWarningWithSourceAndMessage() {
        ClientInputExceptionHandler handler = new ClientInputExceptionHandler();
        String source = "GameRegisterHandler";
        Exception ex = new ClientInputException("Invalid player format");

        Logger logger = (Logger) LoggerFactory.getLogger(ClientInputExceptionHandler.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        handler.handle(source, ex);

        List<ILoggingEvent> logs = appender.list;
        assertFalse(logs.isEmpty());

        ILoggingEvent event = logs.get(0);
        assertEquals(Level.WARN, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("Некорректный ввод от клиента"));
        assertTrue(event.getFormattedMessage().contains("Invalid player format"));
        assertTrue(event.getFormattedMessage().contains(source));
    }
}
