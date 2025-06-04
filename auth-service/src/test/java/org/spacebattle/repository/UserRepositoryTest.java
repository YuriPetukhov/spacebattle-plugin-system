package org.spacebattle.repository;

import org.junit.jupiter.api.Test;
import org.spacebattle.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User user = new User();
        user.setName("testuser");
        user.setPassword("password");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getName());
    }

    @Test
    void testFindByName_existingUser_returnsUser() {
        User user = new User();
        user.setName("uniqueName");
        user.setPassword("pass");
        userRepository.save(user);

        Optional<User> result = userRepository.findByName("uniqueName");

        assertTrue(result.isPresent());
        assertEquals("uniqueName", result.get().getName());
    }

    @Test
    void testFindByName_nonExistingUser_returnsEmpty() {
        Optional<User> result = userRepository.findByName("not-exist");
        assertTrue(result.isEmpty());
    }
}
