package org.spacebattle.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exception.BasicFallbackExceptionHandler;
import org.spacebattle.exception.DefaultExceptionHandlerResolver;
import org.spacebattle.ioc.IoCContainer;

import static org.junit.jupiter.api.Assertions.*;

class IoCExceptionSetupTest {

    private IoCContainer ioc;

    @BeforeEach
    void setUp() {
        ioc = new IoCContainer();
    }

    @Test
    void testExceptionHandlerResolverIsRegistered() {
        IoCExceptionSetup.setup(ioc);
        Object resolved = ioc.resolve("exception-handler-resolver");

        assertNotNull(resolved);
        assertTrue(resolved instanceof DefaultExceptionHandlerResolver);

        DefaultExceptionHandlerResolver resolver = (DefaultExceptionHandlerResolver) resolved;
        assertEquals(BasicFallbackExceptionHandler.class, resolver.resolve("unknown").getClass());
    }
}
