package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingResultDto toBookingResultDto(Booking booking) {
        return new BookingResultDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userMapper.toUserResultDto(booking.getBooker()),
                itemMapper.toItemDto(booking.getItem())
        );
    }

    public List<BookingResultDto> toBookingResultDtoList(Collection<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingResultDto)
                .collect(Collectors.toList());
    }
}
