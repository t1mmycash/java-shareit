package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SortType;
import ru.practicum.shareit.booking.service.BookingRemoteCommand;
import ru.practicum.shareit.util.Constant;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingRemoteCommand command;

    @Autowired
    public BookingController(BookingRemoteCommand command) {
        this.command = command;
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid BookingDto bookingDto) {
        log.info("Received a POST request for the endpoint /bookings with userId_{}", userId);
        return command.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam Boolean approved) {
        log.info("Received a PATCH request for the endpoint /bookings/bookingId with userId_{}", userId);
        return command.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Received a GET request for the endpoint /bookings/bookingId with userId_{}", userId);
        return command.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero(message = Constant.FROM_MUST_BE_POSITIVE_OR_ZERO) @RequestParam(required = false) Integer from,
                                                   @Positive(message = Constant.SIZE_MUST_BE_POSITIVE) @RequestParam(required = false) Integer size) {
        SortType state = SortType.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Received a GET request for the endpoint /bookings/ with userId_{}", userId);
        return command.getAllByBookerId(userId, String.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerId(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                             @PositiveOrZero(message = Constant.FROM_MUST_BE_POSITIVE_OR_ZERO) @RequestParam(required = false) Integer from,
                                             @Positive(message = Constant.SIZE_MUST_BE_POSITIVE) @RequestParam(required = false) Integer size) {
        SortType state = SortType.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Received a GET request for the endpoint /bookings/ with userId_{}", userId);
        return command.getAllByBookerItems(userId, String.valueOf(state), from, size);
    }
}
