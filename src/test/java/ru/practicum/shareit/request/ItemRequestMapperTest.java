package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestMapperTest {
    @Autowired
    private ItemRequestMapper mapper;
    private final LocalDateTime created = LocalDateTime.now();
    private final ItemRequestDto resultRequest = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .created(created)
            .build();

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(created)
                .build();

        ItemRequestDto result = mapper.toItemRequestDto(itemRequest, null);

        assertEquals(resultRequest, result);
    }

    @Test
    void testToItemRequestDto() {
        ItemRequestWithoutResponsesDto itemRequest = ItemRequestWithoutResponsesDto.builder()
                .id(1L)
                .description("description")
                .created(created)
                .build();

        ItemRequestDto result = mapper.toItemRequestDto(itemRequest, null);

        assertEquals(resultRequest, result);
    }
}