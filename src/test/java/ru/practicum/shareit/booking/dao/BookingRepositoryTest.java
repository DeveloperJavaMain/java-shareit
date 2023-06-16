package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    private Sort sort = Sort.by("start");

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(-1, "name1", "mail1@yandex.com"));
        user2 = userRepository.save(new User(-1, "name2", "mail2@yandex.com"));
        item = itemRepository.save(new Item(-1, "itemName", "description", true, user1, null));
        LocalDateTime from = LocalDateTime.now().plusMinutes(1);
        LocalDateTime till = from.plusDays(1);
        booking = bookingRepository.save(new Booking(-1, from, till, item, user2, BookingStatus.APPROVED));
    }

    @Test
    void findByBooker_Id() {
        Page<Booking> res = bookingRepository.findByBooker_Id(user2.getId(), Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void existsByBooker_Id() {
        boolean res = bookingRepository.existsByBooker_Id(user2.getId());
        assertTrue(res);
        res = bookingRepository.existsByBooker_Id(user1.getId());
        assertFalse(res);
    }

    @Test
    void findByItemOwnerId() {
        Page<Booking> res = bookingRepository.findByItemOwnerId(user1.getId(), Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatus() {
        LocalDateTime date = LocalDateTime.now().plusHours(1);
        Booking res = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(item.getId(),
                date,
                BookingStatus.APPROVED,
                Sort.by("start"));
        assertNotNull(res);
        assertEquals(booking, res);
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatus() {
        LocalDateTime date = LocalDateTime.now().minusHours(1);
        Booking res = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(item.getId(),
                date,
                BookingStatus.APPROVED,
                sort);
        assertNotNull(res);
        assertEquals(booking, res);
    }

    @Test
    void findByItemIdInAndStartBeforeAndStatus() {
        LocalDateTime date = LocalDateTime.now().plusHours(1);
        List<Booking> res = bookingRepository.findByItemIdInAndStartBeforeAndStatus(
                List.of(item.getId()), date, BookingStatus.APPROVED,
                sort
        );
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(booking, res.get(0));
    }

    @Test
    void findByItemIdInAndStartAfterAndStatus() {
        LocalDateTime date = LocalDateTime.now().minusHours(1);
        List<Booking> res = bookingRepository.findByItemIdInAndStartAfterAndStatus(
                List.of(item.getId()), date, BookingStatus.APPROVED,
                sort
        );
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(booking, res.get(0));
    }

    @Test
    void findByBookerIdAndEndBefore() {
        LocalDateTime date = LocalDateTime.now().plusDays(2);
        Page<Booking> res = bookingRepository.findByBookerIdAndEndBefore(user2.getId(), date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByBookerIdAndStartAfter() {
        LocalDateTime date = LocalDateTime.now().minusHours(1);
        Page<Booking> res = bookingRepository.findByBookerIdAndStartAfter(user2.getId(), date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfter() {
        LocalDateTime date = LocalDateTime.now().plusHours(1);
        Page<Booking> res = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(user2.getId(), date, date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByBookerIdAndStatus() {
        Page<Booking> res = bookingRepository.findByBookerIdAndStatus(user2.getId(), BookingStatus.APPROVED, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByItemOwnerIdAndEndBefore() {
        LocalDateTime date = LocalDateTime.now().plusDays(2);
        Page<Booking> res = bookingRepository.findByItemOwnerIdAndEndBefore(user1.getId(), date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByItemOwnerIdAndStartAfter() {
        LocalDateTime date = LocalDateTime.now().minusDays(2);
        Page<Booking> res = bookingRepository.findByItemOwnerIdAndStartAfter(user1.getId(), date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfter() {
        LocalDateTime date = LocalDateTime.now().plusHours(2);
        Page<Booking> res = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(user1.getId(), date, date, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void findByItemOwnerIdAndStatus() {
        Page<Booking> res = bookingRepository.findByItemOwnerIdAndStatus(user1.getId(), BookingStatus.APPROVED, Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(booking, res.toList().get(0));
    }

    @Test
    void existsByBookerIdAndEndBefore() {
        LocalDateTime date = LocalDateTime.now().plusDays(2);
        boolean res = bookingRepository.existsByBookerIdAndEndBefore(user2.getId(), date);
        assertTrue(res);
    }
}