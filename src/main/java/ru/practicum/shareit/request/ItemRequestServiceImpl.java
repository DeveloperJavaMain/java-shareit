package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository repository;

    private static final Sort sortDesc = Sort.by("created").descending();

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDtoPost request, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        ItemRequest req = ItemRequestMapper.toItemRequest(request, userId);
        req = repository.save(req);
        return ItemRequestMapper.toItemRequestDto(req);
    }

    @Override
    public ItemRequestDtoResponse getById(long requestId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        ItemRequest req = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest #" + requestId + " not found"));
        List<ItemDtoForRequest> list = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDtoForRequest)
                .collect(Collectors.toList());

        ItemRequestDtoResponse res = ItemRequestMapper.toItemRequestDtoResponse(req, list);
        res.setItems(list);

        return res;
    }

    @Override
    public List<ItemRequestDtoResponse> getByUserId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        List<ItemRequest> list = repository.findByRequestorId(userId, sortDesc);

        return fillItems(list);
    }

    @Override
    public List<ItemRequestDtoResponse> getAll(int from, int size, long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from должно быть положительным, size больше 0");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        Pageable pageable = PageRequest.of(from / size, size, sortDesc);
        Page<ItemRequest> list = repository.findByRequestorIdNot(userId, pageable);

        return fillItems(list.toList());
    }

    private List<ItemRequestDtoResponse> fillItems(List<ItemRequest> list) {
        List<Long> ids = list.stream().map(ItemRequest::getId).collect(Collectors.toList());

        Map<Long, List<ItemDtoForRequest>> mapItems = itemRepository.findAllByRequestIdIn(ids)
                .stream()
                .map(ItemMapper::toItemDtoForRequest)
                .collect(Collectors.groupingBy(c -> c.getRequestId(), Collectors.toList()));

        List<ItemRequestDtoResponse> res = list.stream()
                .map(req -> ItemRequestMapper.toItemRequestDtoResponse(req,
                                mapItems.getOrDefault(req.getId(), List.of())
                        )
                )
                .collect(Collectors.toList());
        return res;
    }
}
