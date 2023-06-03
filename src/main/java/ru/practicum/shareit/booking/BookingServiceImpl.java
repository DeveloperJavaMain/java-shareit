package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, long booker_id) {
        Item item = itemRepository.getReferenceById(bookingDto.getItem_id());
        User user = userRepository.getReferenceById(booker_id);

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAll() {
        List<BookingDto> res = repository.findAll().stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<BookingDto> getListByBooker(long booker_id) {
        List<Booking> list = repository.findByBooker_Id(booker_id, Sort.by(Sort.Direction.DESC, "start"));
        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getListByOwner(long owner_id) {
        List<Booking> list = repository.findByItemOwner(owner_id, Sort.by(Sort.Direction.DESC, "start"));
        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto update(BookingDto bookingDto, long booker_id) {
        Item item = itemRepository.getReferenceById(bookingDto.getItem_id());
        User user = userRepository.getReferenceById(booker_id);

        Booking booking = repository.getReferenceById(bookingDto.getId());
        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }

        repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approve(long booking_id, long user_id, boolean approved) {
        Booking booking = repository.getReferenceById(booking_id);
        if (booking.getBooker().getId() != user_id) {
            throw new ForbiddenException("User #" + user_id + " can't edit booking #" + booking_id);
        }
        booking.setStatus((approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long id, long user_id) {
        Booking booking = repository.getReferenceById(id);
        if (booking.getBooker().getId() != user_id && booking.getItem().getOwner().getId() != user_id) {
            throw new ForbiddenException("User #" + user_id + " can't read booking #" + id);
        }
        return BookingMapper.toBookingDto(repository.getReferenceById(id));
    }

    @Override
    public BookingDto delete(long id) {
        Booking booking = repository.getReferenceById(id);
        repository.deleteById(id);
        return BookingMapper.toBookingDto(booking);
    }
}
