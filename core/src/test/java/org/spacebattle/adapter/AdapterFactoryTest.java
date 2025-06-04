package org.spacebattle.adapter;

import org.junit.jupiter.api.Test;
import org.spacebattle.ioc.IoC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdapterFactoryTest {

    interface Moveable {
        void move(String direction);
    }

    interface Shooter {
        String shoot();
    }

    @Test
    void testVoidMethodDelegatesToIoC() {
        IoC ioc = mock(IoC.class);
        AdapterFactory factory = new AdapterFactory(ioc);
        Moveable adapter = factory.createAdapter(Moveable.class, "ship-1");

        adapter.move("left");

        verify(ioc).resolve("Moveable:move", "ship-1", "left");
    }

    @Test
    void testMethodWithReturnValue() {
        IoC ioc = mock(IoC.class);
        AdapterFactory factory = new AdapterFactory(ioc);
        when(ioc.resolve("Shooter:shoot", "ship-1", new Object[0])).thenReturn("bang");

        Shooter adapter = factory.createAdapter(Shooter.class, "ship-1");

        String result = adapter.shoot();

        assertEquals("bang", result);
        verify(ioc).resolve("Shooter:shoot", "ship-1", new Object[0]);
    }

    @Test
    void testNullArgsHandledGracefully() throws Exception {
        IoC ioc = mock(IoC.class);
        AdapterFactory factory = new AdapterFactory(ioc);

        interface NoArgs { void act(); }

        NoArgs adapter = factory.createAdapter(NoArgs.class, "obj");
        adapter.act();

        verify(ioc).resolve("NoArgs:act", "obj", (Object) null);
    }

}
