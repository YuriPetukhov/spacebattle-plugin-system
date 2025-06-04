package org.spacebattle.setup;

import org.junit.jupiter.api.Test;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.ObjectRepository;

import static org.junit.jupiter.api.Assertions.*;

class IoCRepositorySetupTest {

    @Test
    void testSetupRegistersDefaultObjectRepository() {
        IoCContainer ioc = new IoCContainer();

        IoCRepositorySetup.setup(ioc);

        Object resolved = ioc.resolve("object-repository");
        assertNotNull(resolved);
        assertTrue(resolved instanceof ObjectRepository);
    }
}
