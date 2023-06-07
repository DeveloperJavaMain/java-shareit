package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(BookingDtoPost bookingDto, long bookerId) {

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("End must be after Start");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item #" + bookingDto.getItemId() + " not found"));
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));

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
    public List<BookingDto> getListByBooker(long bookerId, String state) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));
        List<Booking> list = repository.findByBooker_Id(bookerId, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return filterByState(res, state);
    }

    @Override
    public List<BookingDto> getListByOwner(long ownerId, String state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User #" + ownerId + " not found"));
        List<Booking> list = repository.findByItemOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        return filterByState(res, state);
    }

    @Override
    @Transactional
    public BookingDto update(BookingDto bookingDto, long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item #" + bookingDto.getItem().getId() + " not found"));
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));

        Booking booking = repository.getReferenceById(bookingDto.getId());
        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }

        repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking #" + bookingId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
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
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking #" + id + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Not found booking #" + id + " for User #" + userId);
        }
        return BookingMapper.toBookingDto(repository.getReferenceById(id));
    }

    @Override
    @Transactional
    public BookingDto delete(long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking #" + id + " not found"));
        repository.deleteById(id);
        return BookingMapper.toBookingDto(booking);
    }

    private List<BookingDto> filterByState(List<BookingDto> list, String stateName) {

        State state;
        try {
            state = State.valueOf(stateName);
        } catch (IllegalArgumentException e) {
            throw new StatusException("Unknown state: " + stateName);
        }

        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case FUTURE:
                return list.stream().filter(item -> now.isBefore(item.getStart())).collect(Collectors.toList());
            case PAST:
                return list.stream().filter(item -> now.isAfter(item.getEnd())).collect(Collectors.toList());
            case WAITING:
                return list.stream().filter(item -> item.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
            case REJECTED:
                return list.stream().filter(item -> item.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
            case CURRENT:
                log.info("CURRENT before: {}", list);
                List<BookingDto> res = list.stream()
                        .filter(item -> !now.isBefore(item.getStart()))
                        .filter(item -> !now.isAfter(item.getEnd()))
                        .collect(Collectors.toList());
                log.info("CURRENT after: {}", res);
                return res;
            default:
                return list;
        }
    }
}
