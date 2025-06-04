package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.spacebattle.ioc.IoC;
import org.spacebattle.middleware.Middleware;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthPluginBootstrapTest {

    private IoC mockIoC;
    private AuthPluginBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        mockIoC = mock(IoC.class);
        bootstrap = new AuthPluginBootstrap();
        bootstrap.setIoC(mockIoC);
    }

    @Test
    void run_shouldRegisterAccessRegistryInIoC() {
        bootstrap.run();

        verify(mockIoC).resolve(eq("IoC.Register"), eq("access-registry"), any(Function.class));
    }

    @Test
    void run_shouldRegisterAuthMiddlewareInIoC() {
        bootstrap.run();

        verify(mockIoC).resolve(eq("IoC.Register"), eq("middleware:auth"), any(Function.class));
    }

    @Test
    void run_shouldRegisterWorkingAccessRegistryFactory() {
        ArgumentCaptor<Function> captor = ArgumentCaptor.forClass(Function.class);

        bootstrap.run();
        verify(mockIoC).resolve(eq("IoC.Register"), eq("access-registry"), captor.capture());

        Function<Object[], Object> factory = captor.getValue();
        Object registry = factory.apply(new Object[0]);

        assertNotNull(registry);
        assertTrue(registry instanceof AccessRegistry);
    }

    @Test
    void run_shouldRegisterWorkingAuthMiddlewareFactory() {
        ArgumentCaptor<Function> captor = ArgumentCaptor.forClass(Function.class);

        bootstrap.run();
        verify(mockIoC).resolve(eq("IoC.Register"), eq("middleware:auth"), captor.capture());

        Function<Object[], Object> factory = captor.getValue();
        Object middleware = factory.apply(new Object[0]);

        assertNotNull(middleware);
        assertTrue(middleware instanceof Middleware);
    }
}
