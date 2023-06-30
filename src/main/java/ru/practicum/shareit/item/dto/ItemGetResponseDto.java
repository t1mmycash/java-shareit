package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemGetResponseDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentResponseDto> comments;
}
