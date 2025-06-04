package org.spacebattle.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.uobject.IUObject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultObjectRepositoryTest {

    private DefaultObjectRepository repository;
    private IUObject object;

    @BeforeEach
    void setUp() {
        repository = new DefaultObjectRepository();
        object = mock(IUObject.class);
    }

    @Test
    void testSaveAndFindById() {
        repository.save("id1", object);
        Optional<IUObject> found = repository.findById("id1");
        assertTrue(found.isPresent());
        assertEquals(object, found.get());
    }

    @Test
    void testExists() {
        repository.save("id2", object);
        assertTrue(repository.exists("id2"));
        assertFalse(repository.exists("unknown"));
    }

    @Test
    void testDelete() {
        repository.save("id3", object);
        repository.delete("id3");
        assertFalse(repository.exists("id3"));
    }

    @Test
    void testFindByIdReturnsEmptyIfNotFound() {
        Optional<IUObject> found = repository.findById("missing");
        assertTrue(found.isEmpty());
    }
}