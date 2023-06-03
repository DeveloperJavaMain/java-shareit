package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, long ownerId);

    ItemDto getItem(long id);

    List<ItemDto> getAllItems();

    List<ItemDto> getItemsByOwner(long ownerId);

    List<ItemDto> search(String searchText);

    ItemDto updateItem(ItemDto dto, long itemId, long ownerId);

    ItemDto deleteItem(long id, long ownerId);
}
