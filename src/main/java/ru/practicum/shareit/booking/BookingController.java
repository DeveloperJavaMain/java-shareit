package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private BookingService service;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto dto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST " + dto + " userId=" + userId);
        BookingDto bookingDto = service.create(dto, userId);
        return bookingDto;
    }

    @PatchMapping("/{itemId}")
    public BookingDto updateItem(@PathVariable long itemId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH /{} {}", itemId, approved);
        BookingDto dto = service.approve(itemId, userId, approved);
        return dto;
    }

    @GetMapping("/{id}")
    public BookingDto getItemById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /{}", id);
        return service.getById(id, userId);
    }

    @GetMapping
    public List<BookingDto> getList(@RequestParam(required = false, defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /state={}", state);
        List<BookingDto> list = service.getListByBooker(userId);
        return filterByState(list, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getListByOwner(@RequestParam(required = false, defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /state={}", state);
        List<BookingDto> list = service.getListByOwner(userId);
        return filterByState(list, state);
    }

    private List<BookingDto> filterByState(List<BookingDto> list, State state) {
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
            default:
                return list;
        }
    }

}