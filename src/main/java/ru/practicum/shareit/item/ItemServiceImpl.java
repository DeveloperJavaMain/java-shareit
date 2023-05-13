package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private HashMap<Long, Item> items = new HashMap();
    private long counter = 1;
    @Autowired
    private UserService userService;

    @Override
    public ItemDto createItem(ItemDto dto, long ownerId) {
        Item item = ItemMapper.toItem(dto);
        item.setId(counter++);
        item.setOwner(UserMapper.toUser(userService.getUser(ownerId)));
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Item #" + id + " not found");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return items.values().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>();
        }
        String text = searchText.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(ItemDto dto, long itemId, long ownerId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item #" + itemId + " not found");
        }
        if (item.getOwner().getId() != ownerId) {
            throw new ForbiddenException("User #" + ownerId + " can't edit item #" + itemId);
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        item.setOwner(UserMapper.toUser(userService.getUser(ownerId)));
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto deleteItem(long id, long ownerId) {
        Item old = items.get(id);
        if (old == null) {
            return null;
        }
        if (old.getOwner().getId() != ownerId) {
            return null;
        }
        items.remove(id);
        return ItemMapper.toItemDto(old);
    }
}
