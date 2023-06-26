package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addItem(@Validated({Create.class}) @RequestBody ItemDtoPost dto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items {} userId={}", dto, userId);
        return client.createItem(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated({Update.class}) @RequestBody ItemDtoPost dto,
                                             @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /items/{}/", itemId);
        return client.updateItem(dto, itemId, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable long id,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items/{}/", id);
        return client.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0")
                                                  @Min(0) int from,
                                                  @RequestParam(defaultValue = "10")
                                                  @Min(0) int size) {
        log.info("GET /items/ userId={}", userId);
        return client.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(defaultValue = "0")
                                         @Min(0) int from,
                                         @RequestParam(defaultValue = "10")
                                         @Min(0) int size,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items/search");
        return client.search(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoPost text,
                                                @PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items/{}/comment {}", itemId, text);
        return client.createComment(text, itemId, userId);
    }
}
