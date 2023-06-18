package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    UserService userService;

    User user;
    UserDto dto;

    @BeforeEach
    void setUp() {
        user = new User(0, "name", "mail@google.com");
        dto = new UserDto(0, "name", "mail@google.com");
    }

    @Test
    void createUser() {
        UserDto res = userService.createUser(dto);
        assertNotNull(res);
        assertTrue(res.getId() > 0);
        assertEquals(dto.getEmail(), res.getEmail());
    }

    @Test
    void getUser() {
        UserDto res = userService.createUser(dto);
        UserDto user = userService.getUser(res.getId());
        assertNotNull(user);
        assertEquals(res, user);
        assertThrows(NotFoundException.class, () -> userService.getUser(1000));
    }

    @Test
    void getAllUsers() {
        UserDto res = userService.createUser(dto);
        List<UserDto> list = userService.getAllUsers();
        assertNotNull(list);
        assertTrue(list.size() > 0);
        assertEquals(res, list.get(0));
    }

    @Test
    void updateUser() {
        UserDto res = userService.createUser(dto);
        res.setEmail("newmail@yandex.com");
        userService.updateUser(res, res.getId());
        UserDto user = userService.getUser(res.getId());
        assertNotNull(user);
        assertEquals("newmail@yandex.com", user.getEmail());
        res.setEmail("");
        assertThrows(BadRequestException.class, () -> userService.updateUser(res, res.getId()));
        res.setEmail("newmail@yandex.com");
        res.setName("");
        assertThrows(BadRequestException.class, () -> userService.updateUser(res, res.getId()));
    }

    @Test
    void deleteUser() {
        UserDto res = userService.createUser(dto);
        List<UserDto> list = userService.getAllUsers();
        assertTrue(list.contains(res));
        userService.deleteUser(res.getId());
        list = userService.getAllUsers();
        assertFalse(list.contains(res));
    }
}