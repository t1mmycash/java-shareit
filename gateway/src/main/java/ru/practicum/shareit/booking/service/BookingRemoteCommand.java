package ru.practicum.shareit.booking.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingRemoteCommand {

    ResponseEntity<Object> addBooking(Long userId, BookingDto bookingDto);

    ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    ResponseEntity<Object> getBooking(Long userId, Long bookingId);

    ResponseEntity<Object> getAllByBookerId(Long bookerId, String state, Integer from, Integer size);

    ResponseEntity<Object> getAllByBookerItems(Long ownerId, String state, Integer from, Integer size);

}
