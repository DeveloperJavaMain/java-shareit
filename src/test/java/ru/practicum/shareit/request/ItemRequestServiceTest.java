package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService service;
    @Autowired
    private UserService userService;

    private ItemRequestDtoPost post;
    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = userService.createUser(new UserDto(0, "name", "mail@google.com"));
        user2 = userService.createUser(new UserDto(0, "name2", "mail2@google.com"));
        post = new ItemRequestDtoPost("description");
    }

    @Test
    void create() {
        ItemRequestDto dto = service.create(post, user1.getId());
        assertNotNull(dto);
        assertEquals(post.getDescription(), dto.getDescription());
    }

    @Test
    void getById() {
        ItemRequestDto dto = service.create(post, user1.getId());
        ItemRequestDtoResponse res = service.getById(dto.getId(), 1);
        assertNotNull(res);
        assertEquals(dto.getId(), res.getId());
    }

    @Test
    void getByUserId() {
        ItemRequestDto dto = service.create(post, user1.getId());
        List<ItemRequestDtoResponse> res = service.getByUserId(1);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(dto.getId(), res.get(0).getId());
    }

    @Test
    void getAll() {
        ItemRequestDto dto = service.create(post, user1.getId());
        List<ItemRequestDtoResponse> res = service.getAll(0, 100, user2.getId());
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(dto.getId(), res.get(0).getId());
        assertThrows(BadRequestException.class, () -> service.getAll(-10, 100, user2.getId()));
    }
}