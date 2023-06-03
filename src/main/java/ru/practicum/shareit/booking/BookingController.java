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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

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

}