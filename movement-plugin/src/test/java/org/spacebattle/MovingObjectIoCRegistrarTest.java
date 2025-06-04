package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovingObjectIoCRegistrarTest {

    private IoC ioc;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
    }

    @Test
    void testRegister_getLocation() {
        IUObject mockObject = mock(IUObject.class);
        Point point = new Point(1, 2);
        when(mockObject.getProperty("location")).thenReturn(Optional.of(point));

        final Function<Object[], Object>[] registered = new Function[1];
        doAnswer(invocation -> {
            registered[0] = (Function<Object[], Object>) invocation.getArgument(2);
            return null;
        }).when(ioc).resolve(eq("IoC.Register"), eq("MovingObject:getLocation"), any());

        MovingObjectIoCRegistrar.register(ioc);
        Object result = registered[0].apply(new Object[]{mockObject});

        assertEquals(point, result);
    }

    @Test
    void testRegister_getVelocity() {
        IUObject mockObject = mock(IUObject.class);
        Vector velocity = new Vector(3, 4);
        when(mockObject.getProperty("velocity")).thenReturn(Optional.of(velocity));

        final Function<Object[], Object>[] registered = new Function[1];
        doAnswer(invocation -> {
            if (invocation.getArgument(1).equals("MovingObject:getVelocity")) {
                registered[0] = (Function<Object[], Object>) invocation.getArgument(2);
            }
            return null;
        }).when(ioc).resolve(eq("IoC.Register"), eq("MovingObject:getVelocity"), any());

        MovingObjectIoCRegistrar.register(ioc);
        Object result = registered[0].apply(new Object[]{mockObject});

        assertEquals(velocity, result);
    }

    @Test
    void testRegister_setLocation() {
        IUObject mockObject = mock(IUObject.class);
        Point newLocation = new Point(5, 6);

        final Function<Object[], Object>[] registered = new Function[1];
        doAnswer(invocation -> {
            if (invocation.getArgument(1).equals("MovingObject:setLocation")) {
                registered[0] = (Function<Object[], Object>) invocation.getArgument(2);
            }
            return null;
        }).when(ioc).resolve(eq("IoC.Register"), eq("MovingObject:setLocation"), any());

        MovingObjectIoCRegistrar.register(ioc);
        Object result = registered[0].apply(new Object[]{mockObject, newLocation});

        verify(mockObject).setProperty("location", newLocation);
        assertNull(result);
    }

    @Test
    void testGetLocation_throwsIfMissing() {
        IUObject mockObject = mock(IUObject.class);
        when(mockObject.getProperty("location")).thenReturn(Optional.empty());

        final Function<Object[], Object>[] registered = new Function[1];
        doAnswer(invocation -> {
            registered[0] = (Function<Object[], Object>) invocation.getArgument(2);
            return null;
        }).when(ioc).resolve(eq("IoC.Register"), eq("MovingObject:getLocation"), any());

        MovingObjectIoCRegistrar.register(ioc);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registered[0].apply(new Object[]{mockObject}));

        assertEquals("Location not set", ex.getMessage());
    }
}
