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
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.exceptions.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private BookingService service;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDtoPost dto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings {} userId={}", dto, userId);
        BookingDto bookingDto = service.create(dto, userId);
        log.info("{}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{itemId}")
    public BookingDto updateItem(@PathVariable long itemId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} {}", itemId, approved);
        BookingDto dto = service.approve(itemId, userId, approved);
        log.info("{}", dto);
        return dto;
    }

    @GetMapping("/{id}")
    public BookingDto getItemById(@PathVariable long id,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{}", id);
        return service.getById(id, userId);
    }

    @GetMapping
    public List<BookingDto> getList(@RequestParam(defaultValue = "ALL") String state,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0")
                                    @Min(0) int from,
                                    @RequestParam(defaultValue = "10")
                                    @Min(0) int size) {
        log.info("GET /bookings/state={}", state);
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from должно быть положительным, size больше 0");
        }
        List<BookingDto> list = service.getListByBooker(userId, state, from, size);
        return list;
    }

    @GetMapping("/owner")
    public List<BookingDto> getListByOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0")
                                           @Min(0) int from,
                                           @RequestParam(defaultValue = "10")
                                           @Min(0) int size) {
        log.info("GET /bookings/state={}", state);
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from должно быть положительным, size больше 0");
        }
        List<BookingDto> list = service.getListByOwner(userId, state, from, size);
        return list;
    }
}