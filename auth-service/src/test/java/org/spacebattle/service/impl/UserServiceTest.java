package org.spacebattle.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.model.User;
import org.spacebattle.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void exists_shouldReturnTrue_whenUserExists() {
        when(userRepository.findByName("alice")).thenReturn(Optional.of(new User()));

        assertTrue(userService.exists("alice"));
    }

    @Test
    void exists_shouldReturnFalse_whenUserNotExists() {
        when(userRepository.findByName("bob")).thenReturn(Optional.empty());

        assertFalse(userService.exists("bob"));
    }

    @Test
    void register_shouldEncodePasswordAndSaveUser() {
        String rawPassword = "1234";
        String encodedPassword = "ENCODED";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        userService.register("charlie", rawPassword);

        verify(passwordEncoder).encode("1234");
        verify(userRepository).save(argThat(user ->
                user.getName().equals("charlie") &&
                        user.getPassword().equals("ENCODED")
        ));
    }

    @Test
    void validate_shouldReturnTrue_whenPasswordMatches() {
        User user = new User();
        user.setPassword("ENCODED");

        when(userRepository.findByName("dave")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "ENCODED")).thenReturn(true);

        assertTrue(userService.validate("dave", "raw"));
    }

    @Test
    void validate_shouldReturnFalse_whenUserNotFound() {
        when(userRepository.findByName("eve")).thenReturn(Optional.empty());

        assertFalse(userService.validate("eve", "anything"));
    }

    @Test
    void validate_shouldReturnFalse_whenPasswordDoesNotMatch() {
        User user = new User();
        user.setPassword("ENCODED");

        when(userRepository.findByName("frank")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertFalse(userService.validate("frank", "wrong"));
    }

    @Test
    void getAllUsernames_shouldReturnListOfNames() {
        User user1 = new User();
        user1.setName("alice");
        User user2 = new User();
        user2.setName("bob");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<String> names = userService.getAllUsernames();

        assertEquals(List.of("alice", "bob"), names);
    }
}
