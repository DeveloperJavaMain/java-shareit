package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDtoPost item, long ownerId);

    ItemDto getItem(long id, Long userId);

    List<ItemDto> getItemsByOwner(long ownerId, int from, int size);

    List<ItemDto> search(String searchText, int from, int size);

    ItemDto updateItem(ItemDtoPost dto, long itemId, long ownerId);

    ItemDto deleteItem(long id, long ownerId);

    CommentDto createComment(CommentDtoPost text, Long itemId, Long userId);

}
