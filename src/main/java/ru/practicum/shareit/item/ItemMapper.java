package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                null, null, List.of(),
                item.getRequestId()
        );
    }

    public static Item toItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static Item toItem(ItemDtoPost dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setRequestId(dto.getRequestId());
        return item;
    }

    public static ItemDtoForRequest toItemDtoForRequest(Item item) {
        ItemDtoForRequest res = new ItemDtoForRequest();
        res = new ItemDtoForRequest(item.getId(),
                item.getName(),
                item.getOwner().getId(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId());
        return res;
    }
}
