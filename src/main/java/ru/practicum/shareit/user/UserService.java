package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.ConflictException;
import ru.practicum.shareit.exaption.NotFoundException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
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
        return userRepository.save(user);
    }

    public User updateUser(User user, Long id) {
        user.setId(id);
        for (User userRepo : getAll()) {
            if (userRepo.getEmail().equals(user.getEmail()) && !user.getId().equals(userRepo.getId())) {
                throw new ConflictException("This email is already registered.");
            }
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
