package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingRemoteCommandImpl;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @MockBean
    private BookingRemoteCommandImpl bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addBooking_whenNoItemId_thenResponseStatusBadRequest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void addBooking_whenStartIsNull_thenResponseStatusBadRequest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void addBooking_whenEndIsNull_thenResponseStatusBadRequest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void addBooking_whenStartInPast_thenResponseStatusBadRequest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void addBooking_whenEndInPast_thenResponseStatusBadRequest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings/owner?from=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings/owner?size=0")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings/owner?size=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings?from=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings?size=0")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings?size=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }
}