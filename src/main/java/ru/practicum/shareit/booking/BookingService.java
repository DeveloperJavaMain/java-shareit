package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, long booker_id);

    List<BookingDto> getAll();

    List<BookingDto> getListByBooker(long booker_id);

    List<BookingDto> getListByOwner(long owner_id);

    BookingDto update(BookingDto bookingDto, long booker_id);

    BookingDto approve(long booking_id, long user_id, boolean approved);

    BookingDto getById(long id, long user_id);

    BookingDto delete(long id);
}
