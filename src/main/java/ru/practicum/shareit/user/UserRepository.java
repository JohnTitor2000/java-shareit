package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User findUser(Long id);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    List<User> getAllUsers();
}
