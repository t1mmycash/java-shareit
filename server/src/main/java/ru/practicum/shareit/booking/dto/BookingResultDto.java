package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserResultDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingResultDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserResultDto booker;
    ItemDto item;
}
