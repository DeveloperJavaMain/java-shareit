package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final Sort sortDesc = Sort.by("start").descending();
    private final Sort sortAsc = Sort.by("start").ascending();

    @Override
    @Transactional
    public ItemDto createItem(ItemDtoPost dto, long ownerId) {
        Item item = ItemMapper.toItem(dto);
        item.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User #" + ownerId + " not found")));
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id, Long userId) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item #" + id + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        ItemDto res = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            setBookings(res);
        }
        res.setComments(getCommentsForItem(id));
        return res;
    }

    private BookingDtoItem getFirstBookingDtoItem(List<Booking> list) {
        return (list != null && list.size() > 0) ? BookingMapper.toBookingDtoItem(list.get(0)) : null;
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<ItemDto> res = repository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<Long> itemIds = res.stream().map(ItemDto::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<CommentDto>> comments = commentRepository.findByItem_IdIn(itemIds, Sort.by(DESC, "created"))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(c -> c.getItemId(), Collectors.toList()));

        Map<Long, List<Booking>> lastBookings = bookingRepository
                .findByItemIdInAndStartBeforeAndStatus(itemIds, now, BookingStatus.APPROVED, sortDesc)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.toList()));

        Map<Long, List<Booking>> nextBookings = bookingRepository
                .findByItemIdInAndStartAfterAndStatus(itemIds, now, BookingStatus.APPROVED, sortAsc)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.toList()));

        for (ItemDto item : res) {
            item.setComments(comments.get(item.getId()));
            item.setLastBooking(getFirstBookingDtoItem(lastBookings.get(item.getId())));
            item.setNextBooking(getFirstBookingDtoItem(nextBookings.get(item.getId())));
        }
        return res;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText.isBlank()) {
            return List.of();
        }
        List<ItemDto> res = repository.search(searchText)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return res;
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDtoPost dto, long itemId, long ownerId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item #" + itemId + " not found"));
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User #" + ownerId + " not found"));
        if (item.getOwner().getId() != ownerId) {
            throw new ForbiddenException("User #" + ownerId + " can't edit item #" + itemId);
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        item.setOwner(user);
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto deleteItem(long id, long ownerId) {
        Item old = repository.findById(id).orElseThrow();
        if (old.getOwner().getId() != ownerId) {
            throw new ForbiddenException("User #" + ownerId + " can't delete item #" + id);
        }
        repository.deleteById(id);
        return ItemMapper.toItemDto(old);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDtoPost dto, Long itemId, Long userId) {
        Item item = repository.findById(itemId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!bookingRepository.existsByBookerIdAndEndBefore(userId, LocalDateTime.now())) {
            throw new BadRequestException("No Bookings");
        }
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

    private ItemDto setBookings(ItemDto item) {
        LocalDateTime now = LocalDateTime.now();
        Booking last = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(item.getId(), now, BookingStatus.APPROVED, sortDesc);
        Booking next = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(item.getId(), now, BookingStatus.APPROVED, sortAsc);
        item.setLastBooking(BookingMapper.toBookingDtoItem(last));
        item.setNextBooking(BookingMapper.toBookingDtoItem(next));
        return item;
    }
}
