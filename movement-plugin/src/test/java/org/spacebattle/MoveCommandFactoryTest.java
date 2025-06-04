package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.adapter.AdapterFactory;
import org.spacebattle.behavior.MovingObject;
import org.spacebattle.commands.Command;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class MoveCommandFactoryTest {

    private MoveCommandFactory factory;

    @BeforeEach
    void setup() {
        IoC ioc = new IoC() {
            @Override
            public void register(String key, Function<Object[], Object> factory) {

            }

            @Override
            public Object resolve(String key, Object... args) {
                if ("IoC.Register".equals(key)) return null;
                if (key.startsWith("MovingObject:")) return (Function<Object[], Object>) arr -> {
                    IUObject obj = (IUObject) arr[0];
                    return switch (key) {
                        case "MovingObject:getLocation" -> obj.getProperty("location").orElse(null);
                        case "MovingObject:getVelocity" -> obj.getProperty("velocity").orElse(null);
                        case "MovingObject:setLocation" -> {
                            obj.setProperty("location", arr[1]);
                            yield null;
                        }
                        default -> throw new RuntimeException("Not found: " + key);
                    };
                };
                return null;
            }

            @Override
            public boolean contains(String key) {
                return false;
            }
        };
        factory = new MoveCommandFactory();
        factory.setIoC(ioc);
    }

    @Test
    void testCreateCommandWithDxDy() {
        IUObject obj = new DefaultUObject();
        CommandDTO dto = new CommandDTO("move", "ship-1", Map.of("dx", 1, "dy", 2));


        Command command = factory.create(obj, dto);
        assertNotNull(command);
        assertEquals(new Vector(1, 2), obj.getProperty("velocity").orElse(null));
    }

    @Test
    void testCreateCommandWithVelocityMap() {
        IUObject obj = new DefaultUObject();

        CommandDTO dto = new CommandDTO(
                "move",
                "ship-1",
                Map.of("velocity", Map.of("dx", 3, "dy", 4))
        );


        Command command = factory.create(obj, dto);
        assertNotNull(command);
        assertEquals(new Vector(3, 4), obj.getProperty("velocity").orElse(null));
    }

    @Test
    void testCreateCommandWithVelocityObject() {
        IUObject obj = new DefaultUObject();
        Vector v = new Vector(5, 6);
        CommandDTO dto = new CommandDTO("move", "ship-1", Map.of("velocity", v));


        Command command = factory.create(obj, dto);
        assertNotNull(command);
        assertEquals(v, obj.getProperty("velocity").orElse(null));
    }

    @Test
    void testInvalidVelocityFormatThrows() {
        IUObject obj = new DefaultUObject();
        CommandDTO dto = new CommandDTO("move", "ship-1", Map.of("velocity", "invalid"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> factory.create(obj, dto));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testInvalidVelocityMapThrows() {
        IUObject obj = new DefaultUObject();
        CommandDTO dto = new CommandDTO("move", "ship-1", Map.of("velocity", Map.of("dx", "a", "dy", 1)));


        RuntimeException ex = assertThrows(RuntimeException.class, () -> factory.create(obj, dto));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testToInt_withNumber() {
        assertEquals(42, factoryToInt(42));
    }

    @Test
    void testToInt_withString() {
        assertEquals(12, factoryToInt("12"));
    }


    // Вспомогательный метод доступа к toInt
    private int factoryToInt(Object o) {
        try {
            var m = MoveCommandFactory.class.getDeclaredMethod("toInt", Object.class);
            m.setAccessible(true);
            return (int) m.invoke(factory, o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
