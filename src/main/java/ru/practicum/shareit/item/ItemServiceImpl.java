package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository repository;

    @Override
    public ItemDto createItem(ItemDto dto, long ownerId) {
        Item item = ItemMapper.toItem(dto);
        item.setOwner(userRepository.getReferenceById(ownerId));
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id) {
        Item item = repository.getReferenceById(id);
        if (item == null) {
            throw new NotFoundException("Item #" + id + " not found");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<ItemDto> res = repository.findAll().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<ItemDto> res = repository.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> res = repository.search(searchText).
                stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public ItemDto updateItem(ItemDto dto, long itemId, long ownerId) {
        Item item = repository.getReferenceById(itemId);
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
        item.setOwner(userRepository.getReferenceById(ownerId));
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto deleteItem(long id, long ownerId) {
        Item old = repository.getReferenceById(id);
        if (old == null) {
            return null;
        }
        if (old.getOwner().getId() != ownerId) {
            return null;
        }
        repository.deleteById(id);
        return ItemMapper.toItemDto(old);
    }
}
