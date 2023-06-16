package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService requestService;

    @Autowired
    MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private ItemRequestDto dto;
    private ItemRequestDtoPost postDto;
    private ItemRequestDtoResponse response;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        postDto = new ItemRequestDtoPost("description");
        dto = new ItemRequestDto(1, "description", now);
        response = new ItemRequestDtoResponse(1, "description", now, List.of(new ItemDtoForRequest()));
    }

    @Test
    void createRequest() throws Exception {
        when(requestService.create(any(ItemRequestDtoPost.class), anyLong()))
                .thenReturn(dto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(postDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));
    }

    @Test
    void getById() throws Exception {
        when(requestService.getById(anyLong(), anyLong()))
                .thenReturn(response);
        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findAllByUserId() throws Exception {
        when(requestService.getByUserId(anyLong()))
                .thenReturn(List.of(response));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(response))));
    }

    @Test
    void findAll() throws Exception {
        when(requestService.getAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(response));
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(response))));
    }
}