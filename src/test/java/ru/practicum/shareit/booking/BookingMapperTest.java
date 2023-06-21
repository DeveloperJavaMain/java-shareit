package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingDto() {
        LocalDateTime from = LocalDateTime.now().plusMinutes(10);
        LocalDateTime till = from.plusHours(1);
        Booking booking = new Booking(1,
                from, till, new Item(), new User(),
                BookingStatus.APPROVED
        );
        BookingDto dto = BookingMapper.toBookingDto(booking);
        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertNull(BookingMapper.toBookingDto(null));
    }

    @Test
    void toBookingDtoItem() {
        LocalDateTime from = LocalDateTime.now().plusMinutes(10);
        LocalDateTime till = from.plusHours(1);
        Booking booking = new Booking(1,
                from, till, new Item(), new User(),
                BookingStatus.APPROVED
        );
        BookingDtoItem dto = BookingMapper.toBookingDtoItem(booking);
        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertNull(BookingMapper.toBookingDtoItem(null));
    }

    @Test
    void toBooking() {
        LocalDateTime from = LocalDateTime.now().plusMinutes(10);
        LocalDateTime till = from.plusHours(1);
        BookingDtoPost dto = new BookingDtoPost(1L, from, till);
        Item item = new Item();
        item.setId(1);
        Booking booking = BookingMapper.toBooking(dto, item, new User());
        assertNotNull(dto);
        assertEquals(booking.getItem().getId(), dto.getItemId());
        assertNull(BookingMapper.toBooking(null, item, new User()));
    }

}