package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {


    private final BookingService bookingService;

    @PostMapping
    public BookingResultDto addBooking(
            @RequestHeader(Constant.USER_ID_HEADER) long bookerId,
            @RequestBody BookingDto booking) {
        return bookingService.addBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResultDto updateBookingStatus(
            @RequestHeader(value = Constant.USER_ID_HEADER) long ownerId,
            @PathVariable(value = "bookingId") long bookingId,
            @RequestParam(name = "approved") boolean approved) {
        return bookingService.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResultDto getBooking(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "bookingId") long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResultDto> getAllUserBookings(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return bookingService.getAllUserBookings(userId, sort, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResultDto> getAllUserBookedItemsBookings(
            @RequestHeader(value = Constant.USER_ID_HEADER) long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return bookingService.getAllUserBookedItemsBookings(ownerId, sort, from, size);
    }

}
