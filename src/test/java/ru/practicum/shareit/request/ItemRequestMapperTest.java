package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void toItemRequestDto() {
        ItemRequest request = new ItemRequest(1, "description", 1, LocalDateTime.now());
        ItemRequestDto res = ItemRequestMapper.toItemRequestDto(request);
        assertEquals(request.getId(), res.getId());
        assertEquals(request.getDescription(), res.getDescription());
    }

    @Test
    void toItemRequestDtoResponse() {
        ItemRequest request = new ItemRequest(1, "description", 1, LocalDateTime.now());
        ItemRequestDtoResponse res = ItemRequestMapper.toItemRequestDtoResponse(request, null);
        assertEquals(request.getId(), res.getId());
        assertEquals(request.getDescription(), res.getDescription());
        assertEquals(request.getCreated(), res.getCreated());
    }

    @Test
    void toItemRequest() {
        ItemRequestDtoPost dto = new ItemRequestDtoPost("new description");
        ItemRequest res = ItemRequestMapper.toItemRequest(dto, 2);
        assertEquals(2, res.getRequestorId());
        assertEquals("new description", res.getDescription());
    }
}