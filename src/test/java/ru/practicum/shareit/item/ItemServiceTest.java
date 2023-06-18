package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;

    ItemDtoPost post;
    UserDto user1;
    UserDto user2;

    @BeforeEach
    void setUp() {
        post = new ItemDtoPost("name", "description", true, null);
        user1 = userService.createUser(new UserDto(0, "name", "mail@google.com"));
        user2 = userService.createUser(new UserDto(0, "name2", "mail2@google.com"));
    }

    @Test
    void createItem() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        assertNotNull(dto);
        assertEquals(post.getDescription(), dto.getDescription());
    }

    @Test
    void getItem() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        ItemDto res = itemService.getItem(dto.getId(), user1.getId());
        assertNotNull(res);
        assertEquals(dto, res);
    }

    @Test
    void getItemsByOwner() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        List<ItemDto> res = itemService.getItemsByOwner(user1.getId(), 0, 100);
        assertNotNull(res);
        ;
        assertEquals(1, res.size());
        assertEquals(dto, res.get(0));
        assertThrows(BadRequestException.class, () ->
                itemService.getItemsByOwner(user1.getId(), -10, 100));
    }

    @Test
    void search() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        List<ItemDto> res = itemService.search("escr", 0, 100);
        assertNotNull(res);
        ;
        assertEquals(1, res.size());
        assertEquals(dto, res.get(0));
        res = itemService.search("", 0, 100);
        assertEquals(0, res.size());
        assertThrows(BadRequestException.class, () ->
                itemService.search("escr", -10, 100));
    }

    @Test
    void updateItem() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        dto.setDescription("new text");
        itemService.updateItem(
                new ItemDtoPost("newName", "new text", true, null),
                dto.getId(), user1.getId());
        ItemDto res = itemService.getItem(dto.getId(), user1.getId());
        assertNotNull(res);
        assertEquals("new text", res.getDescription());
        assertThrows(ForbiddenException.class, () ->
                itemService.updateItem(
                        new ItemDtoPost("newName", "new text", true, null),
                        dto.getId(), user2.getId()));
    }

    @Test
    void deleteItem() {
        ItemDto dto = itemService.createItem(post, user1.getId());
        ItemDto res = itemService.getItem(dto.getId(), user1.getId());
        assertThrows(ForbiddenException.class, () -> itemService.deleteItem(dto.getId(), user2.getId()));
        itemService.deleteItem(dto.getId(), user1.getId());
        assertThrows(NotFoundException.class, () -> itemService.getItem(dto.getId(), user1.getId()));
    }

    @Test
    void createComment() {
        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime till = from.plusMinutes(10);
        ItemDto dto = itemService.createItem(post, user1.getId());
        BookingDto post = bookingService.create(
                new BookingDtoPost(dto.getId(), from, till),
                user2.getId());
        CommentDtoPost commentPost = new CommentDtoPost("comment");
        CommentDto comment = itemService.createComment(commentPost, dto.getId(), user2.getId());
        assertEquals("comment", comment.getText());
        assertThrows(BadRequestException.class, () ->
                itemService.createComment(commentPost, dto.getId(), user1.getId()));
    }
}