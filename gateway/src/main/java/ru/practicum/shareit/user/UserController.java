package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserGateWay;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserGateWay userGateWay) {
        log.info("Create user with name={}, email={}", userGateWay.getName(), userGateWay.getEmail());
        return userClient.createUser(userGateWay);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") @Positive Long id, @RequestBody UserGateWay userGateWay) {
        log.info("Update user with userId={}, name={}, email={}", id, userGateWay.getName(), userGateWay.getEmail());
        return userClient.updateUser(id, userGateWay);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") @Positive Long id) {
        log.info("Get user with userId={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Object> deleteUser(@PathVariable("id") @Positive Long userId) {
        log.info("Delete user with userId={}", userId);
        return  userClient.deleteUser(userId);
    }
}
