package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserClient client;

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated({Create.class}) @RequestBody UserDto user) {
        log.info("POST /users/ {}", user);
        return client.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated({Update.class}) @RequestBody UserDto user,
                                             @PathVariable long userId) {
        log.info("PATCH /users/ userId={} {}", userId, user);
        return client.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("DELETE /users/ userId={}", userId);
        client.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable long id) {
        log.info("GET /users/{}", id);
        return client.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /users/");
        return client.getAllUsers();
    }
}
