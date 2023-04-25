package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.ConflictException;

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
        if (user.getEmail() == null) {
            throw new BadRequestException("Email is required for registration.");
        }
        for (User userRepo : getAll()) {
            if (userRepo.getEmail().equals(user.getEmail())) {
                throw new ConflictException("This email is already registered.");
            }
        }
        return userRepository.createUser(user);
    }

    public User updateUser(Long id, User user) {
        for (User userRepo : getAll()) {
            if (userRepo.getEmail().equals(user.getEmail()) && !id.equals(userRepo.getId())) {
                throw new ConflictException("This email is already registered.");
            }
        }
        return userRepository.updateUser(id, user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public List<User> getAll() {
        return userRepository.getAllUsers();
    }
}
