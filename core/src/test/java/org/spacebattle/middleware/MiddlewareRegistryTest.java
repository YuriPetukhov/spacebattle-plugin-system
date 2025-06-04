package org.spacebattle.middleware;

import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MiddlewareRegistryTest {

    @Test
    void testSingleMiddlewareWrapsCommand() throws Exception {
        MiddlewareRegistry registry = new MiddlewareRegistry();
        List<String> log = new ArrayList<>();

        registry.register(next -> () -> {
            log.add("before");
            next.execute();
            log.add("after");
        });

        Command base = () -> log.add("core");
        Command wrapped = registry.wrap(base);

        wrapped.execute();

        assertEquals(List.of("before", "core", "after"), log);
    }

    @Test
    void testMultipleMiddlewareWrapInCorrectOrder() throws Exception {
        MiddlewareRegistry registry = new MiddlewareRegistry();
        List<String> log = new ArrayList<>();

        registry.register(next -> () -> {
            log.add("mw1-start");
            next.execute();
            log.add("mw1-end");
        });

        registry.register(next -> () -> {
            log.add("mw2-start");
            next.execute();
            log.add("mw2-end");
        });

        Command base = () -> log.add("core");
        Command wrapped = registry.wrap(base);

        wrapped.execute();

        assertEquals(List.of(
                "mw1-start",
                "mw2-start",
                "core",
                "mw2-end",
                "mw1-end"
        ), log);
    }

    @Test
    void testNoMiddlewareDoesNotChangeCommand() throws Exception {
        MiddlewareRegistry registry = new MiddlewareRegistry();
        List<String> log = new ArrayList<>();

        Command base = () -> log.add("core");
        Command wrapped = registry.wrap(base);

        wrapped.execute();

        assertEquals(List.of("core"), log);
    }
}
