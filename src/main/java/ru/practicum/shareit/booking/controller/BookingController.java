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

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final String userIdNotNull = "id пользователя не может быть null";
    private final String bookingIdNotNull = "id аренды не может быть null";
    private final String approvedNotNull = "id аренды не может быть null";


    private final BookingService bookingService;

    @PostMapping
    public BookingResultDto addBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull(message = userIdNotNull) Long bookerId,
            @RequestBody @Valid BookingDto booking) {
        return bookingService.addBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResultDto updateBookingStatus(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = userIdNotNull) Long ownerId,
            @PathVariable(value = "bookingId") @NotNull(message = bookingIdNotNull) Long bookingId,
            @RequestParam(name = "approved") @NotNull(message = approvedNotNull) Boolean approved) {
        return bookingService.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResultDto getBooking(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = userIdNotNull) Long userId,
            @PathVariable(value = "bookingId") @NotNull(message = bookingIdNotNull) Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResultDto> getAllUserBookings(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = userIdNotNull) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort) {
        return bookingService.getAllUserBookings(userId, sort);
    }

    @GetMapping("/owner")
    public List<BookingResultDto> getAllUserBookedItemsBookings(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = userIdNotNull) Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort) {
        return bookingService.getAllUserBookedItemsBookings(ownerId, sort);
    }

}
