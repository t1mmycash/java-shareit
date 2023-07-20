package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInItemDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    long bookerId;

    public BookingInItemDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status, long bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.bookerId = bookerId;
    }
}
