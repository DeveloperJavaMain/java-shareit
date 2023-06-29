package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.StatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDtoPost dto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings {} userId={}", dto, userId);
        return client.create(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> approve(@PathVariable long itemId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} {}", itemId, approved);
        return client.approve(itemId, userId, approved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{}", id);
        return client.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getList(@RequestParam(defaultValue = "ALL") String state,
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0")
                                          @Min(0) int from,
                                          @RequestParam(defaultValue = "10")
                                          @Min(0) int size) {
        log.info("GET /bookings/state={}", state);
        try {
            State tstate = State.valueOf(state);
            return client.getListByBooker(userId, tstate, from, size);
        } catch (IllegalArgumentException e) {
            throw new StatusException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getListByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @Min(0) int from,
                                                 @RequestParam(defaultValue = "10")
                                                 @Min(0) int size) {
        log.info("GET /bookings/state={}", state);
        try {
            State tstate = State.valueOf(state);
            return client.getListByOwner(userId, tstate, from, size);
        } catch (IllegalArgumentException e) {
            throw new StatusException("Unknown state: " + state);
        }

    }
}