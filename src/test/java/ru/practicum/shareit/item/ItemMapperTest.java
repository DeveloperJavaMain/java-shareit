package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = new Item(1, "name", "description", true, new User(), null);
        ItemDto dto = ItemMapper.toItemDto(item);
        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void toItem() {
        ItemDto dto = new ItemDto("name", "description", true);
        Item item = ItemMapper.toItem(dto);
        assertNotNull(item);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void testToItem() {
        ItemDtoPost dto = new ItemDtoPost("name", "description", true, null);
        Item item = ItemMapper.toItem(dto);
        assertNotNull(item);
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void toItemDtoForRequest() {
        Item item = new Item(1, "name", "description", true, new User(), null);
        ItemDtoForRequest dto = ItemMapper.toItemDtoForRequest(item);
        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getDescription(), dto.getDescription());
    }
}