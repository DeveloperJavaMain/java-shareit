package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private BookingDtoPost dtoPost;
    private BookingDto dto;
    private LocalDateTime from;
    private LocalDateTime till;

    @BeforeEach
    void setUp() {
        from = LocalDateTime.now().plusMinutes(10);
        till = from.plusHours(1);
        dtoPost = new BookingDtoPost(1L, from, till);
        dto = new BookingDto(1, from, till, new ItemDto(), new UserDto(), BookingStatus.APPROVED);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(BookingDtoPost.class), any(Long.class)))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dtoPost))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(dto.getItem()), ItemDto.class));

        verify(bookingService, times(1))
                .create(any(BookingDtoPost.class), any(Long.class));
    }

    @Test
    void approve() throws Exception {
        dto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(dto);
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));
        verify(bookingService, times(1))
                .approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(bookingService, times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getList() throws Exception {
        when(bookingService.getListByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService, times(1))
                .getListByBooker(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getListByOwner() throws Exception {
        when(bookingService.getListByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService, times(1))
                .getListByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }
}