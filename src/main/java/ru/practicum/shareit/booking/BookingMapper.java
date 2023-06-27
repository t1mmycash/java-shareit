package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingResultDto toBookingResultDto(Booking booking) {
        return new BookingResultDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserResultDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static List<BookingResultDto> toBookingResultDtoList(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingResultDto)
                .collect(Collectors.toList());
    }
}
