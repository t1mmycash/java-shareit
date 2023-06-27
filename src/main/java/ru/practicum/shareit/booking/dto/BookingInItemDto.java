package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInItemDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    long bookerId;
}
