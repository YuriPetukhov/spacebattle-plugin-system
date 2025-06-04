package org.spacebattle.service;

import java.util.List;

public interface IUserService {
    boolean exists(String username);

    void register(String username, String rawPassword);

    boolean validate(String username, String rawPassword);

    List<String> getAllUsernames();
}
