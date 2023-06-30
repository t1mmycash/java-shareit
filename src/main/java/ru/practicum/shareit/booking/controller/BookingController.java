package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_ID_NOT_NULL = "id пользователя не может быть null";
    private static final String BOOKING_ID_NOT_NULL = "id аренды не может быть null";
    private static final String APPROVED_NOT_NULL = "id аренды не может быть null";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResultDto addBooking(
            @RequestHeader(USER_ID_HEADER) @NotNull(message = USER_ID_NOT_NULL) Long bookerId,
            @RequestBody @Valid BookingDto booking) {
        return bookingService.addBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResultDto updateBookingStatus(
            @RequestHeader(value = USER_ID_HEADER) @NotNull(message = USER_ID_NOT_NULL) Long ownerId,
            @PathVariable(value = "bookingId") @NotNull(message = BOOKING_ID_NOT_NULL) Long bookingId,
            @RequestParam(name = "approved") @NotNull(message = APPROVED_NOT_NULL) Boolean approved) {
        return bookingService.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResultDto getBooking(
            @RequestHeader(value = USER_ID_HEADER) @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @PathVariable(value = "bookingId") @NotNull(message = BOOKING_ID_NOT_NULL) Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResultDto> getAllUserBookings(
            @RequestHeader(value = USER_ID_HEADER) @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort) {
        return bookingService.getAllUserBookings(userId, sort);
    }

    @GetMapping("/owner")
    public List<BookingResultDto> getAllUserBookedItemsBookings(
            @RequestHeader(value = USER_ID_HEADER) @NotNull(message = USER_ID_NOT_NULL) Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort) {
        return bookingService.getAllUserBookedItemsBookings(ownerId, sort);
    }

}
