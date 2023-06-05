package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
    public BookingDto create(BookingDtoPost bookingDto, long bookerId) {

        LocalDateTime now = LocalDateTime.now();

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("End must be after Start");
        }

        if (bookingDto.getEnd().isBefore(now) || bookingDto.getStart().isBefore(now)) {
            throw new ValidationException("End and Start mast be in Future");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow();
        User user = userRepository.findById(bookerId).orElseThrow();

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Owner can't book item");
        }

        if (!item.isAvailable()) {
            throw new ValidationException("Item is unavailable");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);

        booking = repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAll() {
        List<BookingDto> res = repository.findAll().stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<BookingDto> getListByBooker(long bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow();
        List<Booking> list = repository.findByBooker_Id(bookerId, Sort.by(Sort.Direction.DESC, "start"));
        return list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getListByOwner(long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow();
        List<Booking> list = repository.findByItemOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "start"));
        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto update(BookingDto bookingDto, long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItem().getId()).orElseThrow();
        User user = userRepository.findById(bookerId).orElseThrow();

        Booking booking = repository.getReferenceById(bookingDto.getId());
        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }

        repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = repository.findById(bookingId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("User #" + userId + " can't edit booking #" + bookingId);
        }
        BookingStatus status = (approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus() == status) {
            throw new ValidationException("Already have status " + status);
        }
        booking.setStatus(status);
        booking = repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long id, long userId) {
        Booking booking = repository.findById(id).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Not found booking #" + id + " for User #" + userId);
        }
        return BookingMapper.toBookingDto(repository.getReferenceById(id));
    }

    @Override
    public BookingDto delete(long id) {
        Booking booking = repository.findById(id).orElseThrow();
        repository.deleteById(id);
        return BookingMapper.toBookingDto(booking);
    }
}
