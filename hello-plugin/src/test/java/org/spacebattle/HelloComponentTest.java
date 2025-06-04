package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.plugins.PluginComponent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HelloComponentTest {

    @Test
    void testHelloMessagePrinted() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            PluginComponent component = new HelloComponent();
            component.apply(getClass().getClassLoader(), Map.of());

            String output = out.toString().trim();
            assertTrue(output.contains("Hello from HelloComponent"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
