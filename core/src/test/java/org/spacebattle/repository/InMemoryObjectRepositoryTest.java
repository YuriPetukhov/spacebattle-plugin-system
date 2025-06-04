
package org.spacebattle.repository;

import org.junit.jupiter.api.Test;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryObjectRepositoryTest {

    @Test
    void testSaveAndFind() {
        ObjectRepository repo = new InMemoryObjectRepository();
        IUObject obj = new DefaultUObject();
        obj.setProperty("name", "Ship");

        repo.save("ship-1", obj);

        IUObject found = repo.findById("ship-1").orElseThrow();
        assertEquals("Ship", found.getProperty("name").orElse(null));
    }

    @Test
    void testExists() {
        ObjectRepository repo = new InMemoryObjectRepository();
        repo.save("x", new DefaultUObject());

        assertTrue(repo.exists("x"));
        assertFalse(repo.exists("y"));
    }

    @Test
    void testDelete() {
        ObjectRepository repo = new InMemoryObjectRepository();
        repo.save("z", new DefaultUObject());

        repo.delete("z");
        assertFalse(repo.exists("z"));
    }

    @Test
    void testFindMissingReturnsEmpty() {
        ObjectRepository repo = new InMemoryObjectRepository();

        assertTrue(repo.findById("missing").isEmpty());
    }
}
