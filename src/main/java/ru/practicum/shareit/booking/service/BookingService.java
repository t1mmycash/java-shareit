package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;

import java.util.List;

public interface BookingService {
    BookingResultDto addBooking(long bookerId, BookingDto booking);

    BookingResultDto updateBookingStatus(long ownerId, long bookingId, boolean approved);

    BookingResultDto getBooking(long userId, long bookingId);

    List<BookingResultDto> getAllUserBookings(long userId, String sort, int from, int size);

    List<BookingResultDto> getAllUserBookedItemsBookings(long userId, String sort, int from, int size);
}
