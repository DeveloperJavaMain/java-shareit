package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        ItemDto res = ItemMapper.toItemDto(item);
        setBookings(res);
        res.setComments(getCommentsForItem(id));
        return res;
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<ItemDto> res = repository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .map(item -> {
                    item.setComments(getCommentsForItem(item.getId()));
                    return item;
                })
                .collect(Collectors.toList());
        return res;
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<ItemDto> res = repository.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());

        for (ItemDto item : res) {
            setBookings(item);
        }
        return res;
    }

    private final Sort sortDesc = Sort.by("start").descending();
    private final Sort sortAsc = Sort.by("start").ascending();

    private ItemDto setBookings(ItemDto item) {
        LocalDateTime now = LocalDateTime.now();
        Booking last = bookingRepository.findFirstByItemIdAndEndBefore(item.getId(), now, sortDesc);
        Booking next = bookingRepository.findFirstByItemIdAndStartAfter(item.getId(), now, sortAsc);
        item.setLastBooking(BookingMapper.toBookingDtoItem(last));
        item.setNextBooking(BookingMapper.toBookingDtoItem(next));
        return item;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> res = repository.search(searchText).
                stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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

    @Override
    public CommentDto createComment(CommentDtoPost dto, Long itemId, Long userId) {
        Item item = repository.findById(itemId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Booking booking = bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now())
                .stream().findAny().orElseThrow();
        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private List<CommentDto> getCommentsForItem(Long item_id) {
        List<CommentDto> res = commentRepository.findByItemId(item_id).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return res;
    }

}
