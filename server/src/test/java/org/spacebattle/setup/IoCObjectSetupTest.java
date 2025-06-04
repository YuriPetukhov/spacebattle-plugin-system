package org.spacebattle.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.factory.ObjectFactory;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.ObjectRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoCObjectSetupTest {

    private IoCContainer ioc;
    private ObjectRepository repository;

    @BeforeEach
    void setUp() {
        ioc = new IoCContainer();
        repository = mock(ObjectRepository.class);
        ioc.register("object-repository", args -> repository);
    }

    @Test
    void testSetupRegistersObjectFactory() {
        IoCObjectSetup.setup(ioc);

        Object resolved = ioc.resolve("object-factory");
        assertNotNull(resolved);
        assertTrue(resolved instanceof ObjectFactory);
    }
}
