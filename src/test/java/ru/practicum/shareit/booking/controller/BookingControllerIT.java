package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addBooking_whenInvoked_thenResponseStatusOkAndBookingResultDtoInBody() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(bookingService.addBooking(1L, bookingDto)).thenReturn(bookingResultDto);

        String result = mvc.perform(post("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResultDto), result);
        verify(bookingService, times(1)).addBooking(1L, bookingDto);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void addBooking_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(post("/bookings"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }


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
    void updateBookingStatus_whenInvoked_thenResponseStatusOkAndUpdatedBookingResultDtoInBody() {
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(bookingService.updateBookingStatus(1L, 1L, true))
                .thenReturn(bookingResultDto);

        String result = mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResultDto), result);
        verify(bookingService, times(1)).updateBookingStatus(1L, 1L, true);
        verifyNoMoreInteractions(bookingService);
    }


    @Test
    @SneakyThrows
    void updateBookingStatus_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getBooking_whenInvoked_thenResponseStatusOkAndBookingResultDtoInBody() {
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(bookingService.getBooking(1L, 1L)).thenReturn(bookingResultDto);

        String result = mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingResultDto), result);
        verify(bookingService, times(1)).getBooking(1L, 1L);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getBooking_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings/{bookingId}", 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenInvokedWithFromAndSizeAndState_thenResponseStatusOkAndBookingResultDtoListInBody() {
        List<BookingResultDto> bookings = List.of(
                BookingResultDto.builder().id(1L).build(),
                BookingResultDto.builder().id(2L).build());
        when(bookingService.getAllUserBookedItemsBookings(1L, "APPROVED", 10, 30))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner?state=APPROVED&from=10&size=30")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
        verify(bookingService, times(1))
                .getAllUserBookedItemsBookings(1L, "APPROVED", 10, 30);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenInvokedWithoutFromAndSizeAndState_thenResponseStatusOkAndBookingResultDtoListInBody() {
        List<BookingResultDto> bookings = List.of(
                BookingResultDto.builder().id(1L).build(),
                BookingResultDto.builder().id(2L).build());
        when(bookingService.getAllUserBookedItemsBookings(1L, "ALL", 0, 20))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
        verify(bookingService, times(1))
                .getAllUserBookedItemsBookings(1L, "ALL", 0, 20);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookedItemsBookings_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings/owner"))
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
    void getAllUserBookings_whenInvokedWithFromAndSizeAndState_thenResponseStatusOkAndBookingResultDtoListInBody() {
        List<BookingResultDto> bookings = List.of(
                BookingResultDto.builder().id(1L).build(),
                BookingResultDto.builder().id(2L).build());
        when(bookingService.getAllUserBookings(1L, "APPROVED", 10, 30))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings?state=APPROVED&from=10&size=30")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
        verify(bookingService, times(1))
                .getAllUserBookings(1L, "APPROVED", 10, 30);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenInvokedWithoutFromAndSizeAndState_thenResponseStatusOkAndBookingResultDtoListInBody() {
        List<BookingResultDto> bookings = List.of(
                BookingResultDto.builder().id(1L).build(),
                BookingResultDto.builder().id(2L).build());
        when(bookingService.getAllUserBookings(1L, "ALL", 0, 20))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
        verify(bookingService, times(1))
                .getAllUserBookings(1L, "ALL", 0, 20);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/bookings"))
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