package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDtoPost request, long userId);

    ItemRequestDtoResponse getById(long requestId, long userId);

    List<ItemRequestDtoResponse> getByUserId(long userId);

    List<ItemRequestDtoResponse> getAll(int from, int size, long userId);
}
