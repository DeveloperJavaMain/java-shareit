package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoPost bookingDto, long bookerId);

    List<BookingDto> getAll();

    List<BookingDto> getListByBooker(long bookerId);

    List<BookingDto> getListByOwner(long ownerId);

    BookingDto update(BookingDto bookingDto, long bookerId);

    BookingDto approve(long bookingId, long userId, boolean approved);

    BookingDto getById(long id, long userId);

    BookingDto delete(long id);
}
