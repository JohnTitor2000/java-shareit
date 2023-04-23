package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.ConflictException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private Long id = Long.valueOf(1);
    Map<Long, User> userData = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new BadRequestException("Email is required for registration.");
        }
        for (User userRepo : userData.values()) {
            if (userRepo.getEmail().equals(user.getEmail())) {
                throw new ConflictException("This email is already registered.");
            }
        }
        user.setId(getNextId());
        userData.put(user.getId(), user);
        return userData.get(user.getId());
    }

    @Override
    public User findUser(Long id) {
        return userData.get(id);
    }

    @Override
    public User updateUser(Long id, User user) {
        User patchedUser = userData.get(id);
        if (user.getEmail() != null) {
            for (User userRepo : userData.values()) {
                if (userRepo.getEmail().equals(user.getEmail()) && patchedUser.getId() != userRepo.getId()) {
                    throw new ConflictException("This email is already registered.");
                }
            }
            patchedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            patchedUser.setName(user.getName());
        }
        userData.put(patchedUser.getId(), patchedUser);
        return patchedUser;
    }

    @Override
    public void deleteUser(Long id) {
        userData.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userData.values().stream().collect(Collectors.toList());
    }

    private Long getNextId() {
        return id++;
    }
}
