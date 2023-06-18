package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;

    ItemDtoPost itemDtoPost;
    UserDto user1;
    UserDto user2;
    UserDto user3;
    LocalDateTime from;
    LocalDateTime till;
    ItemDto itemDto;
    BookingDtoPost bookingDtoPost;

    @BeforeEach
    void setUp() {
        itemDtoPost = new ItemDtoPost("name", "description", true, null);
        user1 = userService.createUser(new UserDto(0, "name", "mail@google.com"));
        user2 = userService.createUser(new UserDto(0, "name2", "mail2@google.com"));
        user3 = userService.createUser(new UserDto(0, "name3", "mail3@google.com"));
        itemDto = itemService.createItem(itemDtoPost, user1.getId());

        from = LocalDateTime.now().minusHours(1);
        till = from.plusMinutes(10);
        bookingDtoPost = new BookingDtoPost(itemDto.getId(), from, till);
    }

    @Test
    void create() {
        BookingDto dto = bookingService.create(bookingDtoPost, user2.getId());
        assertNotNull(dto);
        assertEquals(bookingDtoPost.getItemId(), dto.getItem().getId());
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDtoPost, user1.getId()));
        itemDtoPost.setAvailable(false);
        itemService.updateItem(itemDtoPost, itemDto.getId(), user1.getId());
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoPost, user2.getId()));
        bookingDtoPost = new BookingDtoPost(itemDto.getId(), till, from);
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoPost, user1.getId()));
    }

    @Test
    void getListByBooker() {
        BookingDto dto = bookingService.create(bookingDtoPost, user2.getId());
        List<BookingDto> res = bookingService.getListByBooker(user2.getId(),
                "ALL", 0, 100);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(dto.getId(), res.get(0).getId());
        res = bookingService.getListByBooker(user1.getId(),
                "PAST", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByBooker(user1.getId(),
                "FUTURE", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByBooker(user1.getId(),
                "WAITING", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByBooker(user1.getId(),
                "REJECTED", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByBooker(user1.getId(),
                "CURRENT", 0, 100);
        assertEquals(0, res.size());

        assertThrows(BadRequestException.class, () -> bookingService.getListByBooker(user2.getId(),
                "PAST", -1, 100));
    }

    @Test
    void getListByOwner() {
        BookingDto dto = bookingService.create(bookingDtoPost, user2.getId());
        List<BookingDto> res = bookingService.getListByOwner(user1.getId(),
                "ALL", 0, 100);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(dto.getId(), res.get(0).getId());
        res = bookingService.getListByOwner(user2.getId(),
                "PAST", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByOwner(user2.getId(),
                "FUTURE", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByOwner(user2.getId(),
                "WAITING", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByOwner(user2.getId(),
                "REJECTED", 0, 100);
        assertEquals(0, res.size());
        res = bookingService.getListByOwner(user2.getId(),
                "CURRENT", 0, 100);
        assertEquals(0, res.size());
        assertThrows(BadRequestException.class, () -> bookingService.getListByOwner(user2.getId(),
                "PAST", -1, 100));
        assertThrows(StatusException.class, () -> bookingService.getListByOwner(user2.getId(),
                "UNKNOWN", 0, 100));
    }

    @Test
    void approve() {
        BookingDto dto = bookingService.create(bookingDtoPost, user2.getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
        bookingService.approve(dto.getId(), user1.getId(), true);
        BookingDto res = bookingService.getById(dto.getId(), user1.getId());
        assertEquals(BookingStatus.APPROVED, res.getStatus());
        //assertThrows(NotFoundException.class, () -> bookingService.approve(dto.getId(), 10, true));
        assertThrows(NotFoundException.class, () -> bookingService.approve(dto.getId(), user2.getId(), true));
        assertThrows(ValidationException.class, () -> bookingService.approve(dto.getId(), user1.getId(), true));
    }

    @Test
    void getById() {
        BookingDto dto = bookingService.create(bookingDtoPost, user2.getId());
        BookingDto res = bookingService.getById(dto.getId(), user1.getId());
        assertEquals(dto.getId(), res.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getById(dto.getId(), user3.getId()));
    }
}