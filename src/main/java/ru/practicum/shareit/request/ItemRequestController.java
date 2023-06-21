package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@Validated({Create.class})
                                        @RequestBody ItemRequestDtoPost request,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /requests {}", request);
        return service.create(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{}/", requestId);
        return service.getById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/");
        return service.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAll(@RequestParam(defaultValue = "0")
                                                @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10")
                                                @Positive int size,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/all?from={}&size={}", from, size);
        return service.getAll(from, size, userId);
    }
}
