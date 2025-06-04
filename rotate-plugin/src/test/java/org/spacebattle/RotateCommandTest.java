package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.entity.Angle;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;
import org.spacebattle.adapter.AdapterFactory;
import org.spacebattle.behavior.RotatingObject;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RotateCommandTest {

    private RotatingObject createAdapter(IUObject obj) {
        IoCContainer ioc = new IoCContainer();

        ioc.register("IoC.Register", (args) -> {
            String key = (String) args[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> supplier = (Function<Object[], Object>) args[1];
            ioc.register(key, supplier);
            return null;
        });
        RotatingObjectIoCRegistrar.register(ioc);
        return new AdapterFactory(ioc).createAdapter(RotatingObject.class, obj);
    }

    @Test
    void testRotateAddsAngle() {
        IUObject object = new DefaultUObject();
        object.setProperty("angle", new Angle(45, 360));

        RotatingObject ro = createAdapter(object);

        RotateCommand command = new RotateCommand(ro, new Angle(90, 360));
        command.execute();

        assertEquals(new Angle(135, 360), object.getProperty("angle").orElse(null));
    }

    @Test
    void testRotateWrapsAngleOver360() {
        IUObject object = new DefaultUObject();
        object.setProperty("angle", new Angle(300, 360));

        RotatingObject ro = createAdapter(object);

        RotateCommand command = new RotateCommand(ro, new Angle(90, 360));
        command.execute();

        assertEquals(new Angle(30, 360), object.getProperty("angle").orElse(null));
    }

    @Test
    void testRotateDefaultsToZeroAngle() {
        IUObject object = new DefaultUObject();

        object.setProperty("angle", new Angle(0, 360));
        RotatingObject ro = createAdapter(object);

        RotateCommand command = new RotateCommand(ro, new Angle(45, 360));
        command.execute();

        assertEquals(new Angle(45, 360), object.getProperty("angle").orElse(null));
    }
}
