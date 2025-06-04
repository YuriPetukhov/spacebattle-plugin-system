package org.spacebattle.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultObjectFactoryTest {

    private IoCContainer ioc;
    private ObjectRepository repository;
    private DefaultObjectFactory factory;

    @BeforeEach
    void setup() {
        ioc = mock(IoCContainer.class);
        repository = mock(ObjectRepository.class);
        factory = new DefaultObjectFactory(ioc, repository);
    }

    @Test
    void getOrCreate_shouldReturnExistingObject_whenFoundInIoC() {
        IUObject expected = mock(IUObject.class);
        when(ioc.resolve("object:ship-1")).thenReturn(expected);

        IUObject actual = factory.getOrCreate("ship-1");

        assertSame(expected, actual);
        verify(repository, never()).save(anyString(), any());
        verify(ioc, never()).register(anyString(), any());
    }

    @Test
    void getOrCreate_shouldCreateAndRegisterNewObject_whenNotFoundInIoC() {
        when(ioc.resolve("object:ship-2")).thenThrow(new RuntimeException("Not found"));

        IUObject result = factory.getOrCreate("ship-2");

        assertNotNull(result);
        assertInstanceOf(DefaultUObject.class, result);

        verify(repository).save(eq("ship-2"), eq(result));
        verify(ioc).register(eq("object:ship-2"), any(Function.class));
    }
}
