package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private Long id = 1L;
    private Map<Long, User> userData = new HashMap<>();

    @Override
    public User createUser(User user) {
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
            patchedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            patchedUser.setName(user.getName());
        }
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
