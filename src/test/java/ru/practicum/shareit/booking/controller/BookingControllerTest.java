package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;
    private final User owner = new User(1L, "Ivan", "iv@mail.ru");
    private final User booker = new User(2L, "Eva", "eva@mail.ru");
    private final Item item = new Item(1L, "черенок", "отличный черенок", true,
            owner,
            null);

    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1L,
            LocalDateTime.now().plusDays(1L),
            LocalDateTime.now().plusDays(2L),
            item,
            booker, BookingStatus.WAITING);
    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1L,
            LocalDateTime.now().plusDays(1L),
            LocalDateTime.now().plusDays(2L),
            item.getId(),
            booker.getId(), BookingStatus.WAITING);

    @Test
    void add() throws Exception {
        when(bookingService.add(anyLong(), any())).thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOut.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOut.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void changeBookingStatus() throws Exception {
        bookingDtoOut.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOut.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOut.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        bookingDtoOut.setStatus(BookingStatus.WAITING);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOut.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOut.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getAllUserBookings() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDtoOut));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoOut.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDtoOut.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDtoOut));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoOut.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDtoOut.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())));
    }
}