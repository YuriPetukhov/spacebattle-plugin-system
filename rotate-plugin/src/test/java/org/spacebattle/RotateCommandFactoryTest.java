package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.entity.Angle;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RotateCommandFactoryTest {

    private RotateCommandFactory createInitializedFactory() {
        IoCContainer ioc = new IoCContainer();

        ioc.register("IoC.Register", (args) -> {
            String key = (String) args[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> supplier = (Function<Object[], Object>) args[1];
            ioc.register(key, supplier);
            return null;
        });

        RotatingObjectIoCRegistrar.register(ioc);

        RotateCommandFactory factory = new RotateCommandFactory();
        factory.setIoC(ioc);
        return factory;
    }

    @Test
    void testFactoryCreatesCommandWithCorrectAngle() throws Exception {
        IUObject obj = new DefaultUObject();
        obj.setProperty("angle", new Angle(0, 360));

        CommandDTO dto = new CommandDTO("anon", "rotate", Map.of("angle", 45));
        RotateCommandFactory factory = createInitializedFactory();

        factory.create(obj, dto).execute();

        assertEquals(new Angle(45, 360), obj.getProperty("angle").orElse(null));
    }

    @Test
    void testFactoryUsesDefaultAngleIfMissing() throws Exception {
        IUObject obj = new DefaultUObject();
        obj.setProperty("angle", new Angle(0, 360));

        CommandDTO dto = new CommandDTO("anon", "rotate", Map.of());
        RotateCommandFactory factory = createInitializedFactory();

        factory.create(obj, dto).execute();

        assertEquals(new Angle(90, 360), obj.getProperty("angle").orElse(null));
    }
}
