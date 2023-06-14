package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final Sort sortDesc = Sort.by(Sort.Direction.DESC, "start");

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
    public List<BookingDto> getListByBooker(long bookerId, String stateName) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));
        List<Booking> list;

        State state = getState(stateName);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case FUTURE:
                list = repository.findByBookerIdAndStartAfter(bookerId, now, sortDesc);
                break;
            case PAST:
                list = repository.findByBookerIdAndEndBefore(bookerId, now, sortDesc);
                break;
            case WAITING:
                list = repository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sortDesc);
                break;
            case REJECTED:
                list = repository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sortDesc);
                break;
            case CURRENT:
                list = repository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, sortDesc);
                break;
            default:
                list = repository.findByBooker_Id(bookerId, sortDesc);
        }

        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return res;
    }

    @Override
    public List<BookingDto> getListByOwner(long ownerId, String stateName) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User #" + ownerId + " not found"));
        List<Booking> list;

        State state = getState(stateName);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case FUTURE:
                list = repository.findByItemOwnerIdAndStartAfter(ownerId, now, sortDesc);
                break;
            case PAST:
                list = repository.findByItemOwnerIdAndEndBefore(ownerId, now, sortDesc);
                break;
            case WAITING:
                list = repository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sortDesc);
                break;
            case REJECTED:
                list = repository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sortDesc);
                break;
            case CURRENT:
                list = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, sortDesc);
                break;
            default:
                list = repository.findByItemOwnerId(ownerId, sortDesc);
        }

        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return res;
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

    private static State getState(String stateName) {
        State state;
        try {
            state = State.valueOf(stateName);
        } catch (IllegalArgumentException e) {
            throw new StatusException("Unknown state: " + stateName);
        }
        return state;
    }
}
