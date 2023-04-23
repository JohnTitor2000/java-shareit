package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findUser(id);
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(Long id, User user) {
        return userRepository.updateUser(id, user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public List<User> getAll() {
        return userRepository.getAllUsers();
    }
}
