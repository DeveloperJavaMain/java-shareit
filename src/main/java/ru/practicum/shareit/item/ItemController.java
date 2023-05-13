package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private ItemService service;
    @Autowired
    private UserService userService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST " + dto + " userId=" + userId);

        UserDto user = userService.getUser(userId);
        return service.createItem(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto dto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH /{}/", itemId);
        UserDto user = userService.getUser(userId);
        return service.updateItem(dto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("DELETE /{}/", itemId);
        UserDto user = userService.getUser(userId);
        service.deleteItem(itemId, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        log.info("GET /{}/", id);
        return service.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET / userId=" + userId);
        UserDto user = userService.getUser(userId);
        return service.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("GET /search");
        return service.search(text);
    }

}
