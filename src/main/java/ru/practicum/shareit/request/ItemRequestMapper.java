package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto res = new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated());
        return res;
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest request,
                                                                  List<ItemDtoForRequest> list) {
        ItemRequestDtoResponse res = new ItemRequestDtoResponse(request.getId(),
                request.getDescription(),
                request.getCreated(),
                list);
        return res;
    }

    public static ItemRequest toItemRequest(ItemRequestDtoPost request, long userId) {
        ItemRequest res = new ItemRequest();
        res.setDescription(request.getDescription());
        res.setRequestorId(userId);
        res.setCreated(LocalDateTime.now());
        return res;
    }
}
