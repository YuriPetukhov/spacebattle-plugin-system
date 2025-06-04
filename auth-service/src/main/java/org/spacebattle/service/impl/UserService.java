package org.spacebattle.service.impl;

import org.spacebattle.model.User;
import lombok.RequiredArgsConstructor;
import org.spacebattle.repository.UserRepository;
import org.spacebattle.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link IUserService} для управления пользователями.
 * <p>Предоставляет методы для регистрации, проверки существования, валидации и получения списка имён.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Проверяет, существует ли пользователь с заданным именем.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    @Override
    public boolean exists(String username) {
        return userRepository.findByName(username).isPresent();
    }

    /**
     * Регистрирует нового пользователя, кодируя пароль и сохраняя его в БД.
     *
     * @param username    имя пользователя
     * @param rawPassword незашифрованный пароль
     */
    @Override
    public void register(String username, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        User user = new User();
        user.setName(username);
        user.setPassword(encoded);
        userRepository.save(user);
    }

    /**
     * Проверяет правильность пароля для заданного пользователя.
     *
     * @param username    имя пользователя
     * @param rawPassword незашифрованный пароль
     * @return true, если пользователь существует и пароль совпадает
     */
    @Override
    public boolean validate(String username, String rawPassword) {
        return userRepository.findByName(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    /**
     * Возвращает список имён всех зарегистрированных пользователей.
     *
     * @return список имён пользователей
     */
    @Override
    public List<String> getAllUsernames() {
        return userRepository.findAll()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }
}
