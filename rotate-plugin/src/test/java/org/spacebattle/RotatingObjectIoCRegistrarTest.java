package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.entity.Angle;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RotatingObjectIoCRegistrarTest {

    private IoC mockIoC;

    @BeforeEach
    void setUp() {
        mockIoC = mock(IoC.class);
    }

    @Test
    void testGetAngleFunctionRegisteredAndWorks() {
        IUObject obj = mock(IUObject.class);
        Angle angle = new Angle(90, 360);
        when(obj.getProperty("angle")).thenReturn(Optional.of(angle));

        Function<Object[], Object>[] registered = new Function[1];
        doAnswer(inv -> {
            registered[0] = (Function<Object[], Object>) inv.getArgument(2);
            return null;
        }).when(mockIoC).resolve(eq("IoC.Register"), eq("RotatingObject:getAngle"), any());

        RotatingObjectIoCRegistrar.register(mockIoC);

        Object result = registered[0].apply(new Object[]{obj});
        assertEquals(angle, result);
    }

    @Test
    void testSetAngleFunctionRegisteredAndWorks() {
        IUObject obj = mock(IUObject.class);
        Angle angle = new Angle(45, 360);

        Function<Object[], Object>[] registered = new Function[1];
        doAnswer(inv -> {
            registered[0] = (Function<Object[], Object>) inv.getArgument(2);
            return null;
        }).when(mockIoC).resolve(eq("IoC.Register"), eq("RotatingObject:setAngle"), any());

        RotatingObjectIoCRegistrar.register(mockIoC);

        Object result = registered[0].apply(new Object[]{obj, angle});
        verify(obj).setProperty("angle", angle);
        assertNull(result);
    }

    @Test
    void testGetAngularVelocityFunctionRegisteredAndWorks() {
        IUObject obj = mock(IUObject.class);
        Angle angularVelocity = new Angle(5, 360);
        when(obj.getProperty("angularVelocity")).thenReturn(Optional.of(angularVelocity));

        Function<Object[], Object>[] registered = new Function[1];
        doAnswer(inv -> {
            registered[0] = (Function<Object[], Object>) inv.getArgument(2);
            return null;
        }).when(mockIoC).resolve(eq("IoC.Register"), eq("RotatingObject:getAngularVelocity"), any());

        RotatingObjectIoCRegistrar.register(mockIoC);

        Object result = registered[0].apply(new Object[]{obj});
        assertEquals(angularVelocity, result);
    }

    @Test
    void testGetAngleThrowsIfNotPresent() {
        IUObject obj = mock(IUObject.class);
        when(obj.getProperty("angle")).thenReturn(Optional.empty());

        Function<Object[], Object>[] registered = new Function[1];
        doAnswer(inv -> {
            registered[0] = (Function<Object[], Object>) inv.getArgument(2);
            return null;
        }).when(mockIoC).resolve(eq("IoC.Register"), eq("RotatingObject:getAngle"), any());

        RotatingObjectIoCRegistrar.register(mockIoC);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registered[0].apply(new Object[]{obj}));
        assertEquals("Angle not set", ex.getMessage());
    }

    @Test
    void testGetAngularVelocityThrowsIfNotPresent() {
        IUObject obj = mock(IUObject.class);
        when(obj.getProperty("angularVelocity")).thenReturn(Optional.empty());

        Function<Object[], Object>[] registered = new Function[1];
        doAnswer(inv -> {
            registered[0] = (Function<Object[], Object>) inv.getArgument(2);
            return null;
        }).when(mockIoC).resolve(eq("IoC.Register"), eq("RotatingObject:getAngularVelocity"), any());

        RotatingObjectIoCRegistrar.register(mockIoC);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registered[0].apply(new Object[]{obj}));
        assertEquals("Angular velocity not set", ex.getMessage());
    }
}
