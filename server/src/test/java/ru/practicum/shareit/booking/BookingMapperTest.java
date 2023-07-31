package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResultDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;
    private Booking booking;
    private BookingResultDto resultBooking;

    @BeforeEach
    void beforeEach() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(5);
        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(User.builder()
                        .id(1L)
                        .name("name")
                        .email("email@gmail.com")
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("itemName")
                        .description("description")
                        .available(true)
                        .build())
                .build();
        resultBooking = BookingResultDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(UserResultDto.builder()
                        .id(1L)
                        .name("name")
                        .email("email@gmail.com")
                        .build())
                .item(ItemDto.builder()
                        .id(1L)
                        .name("itemName")
                        .description("description")
                        .available(true)
                        .build())
                .build();
    }

    @Test
    void toBookingResultDto() {
        BookingResultDto result = bookingMapper.toBookingResultDto(booking);

        assertEquals(resultBooking, result);
    }

    @Test
    void toBookingResultDtoList() {
        List<BookingResultDto> result = bookingMapper.toBookingResultDtoList(List.of(booking));

        assertEquals(List.of(resultBooking), result);
    }
}