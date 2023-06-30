package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;

import java.util.List;

public interface BookingService {
    public BookingResultDto addBooking(long bookerId, BookingDto booking);

    public BookingResultDto updateBookingStatus(long ownerId, long bookingId, boolean approved);

    public BookingResultDto getBooking(long userId, long bookingId);

    public List<BookingResultDto> getAllUserBookings(long userId, String sort);

    public List<BookingResultDto> getAllUserBookedItemsBookings(long userId, String sort);
}
