package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    private Item item;

    @BeforeEach
    void beforeEach() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    @Test
    void toItemDto() {
        ItemDto resultItem = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto result = itemMapper.toItemDto(item);

        assertEquals(resultItem, result);
    }

    @Test
    void toItemPostDtoWhenRequestIsNull() {
        ItemPostDto resultItem = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        ItemPostDto result = itemMapper.toItemPostDto(item);

        assertEquals(resultItem, result);
    }

    @Test
    void toItemPostDtoWhenRequestNotNull() {
        ItemPostDto resultItem = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        item.setRequest(ItemRequest.builder()
                .id(1L)
                .build());

        ItemPostDto result = itemMapper.toItemPostDto(item);

        assertEquals(resultItem, result);
    }

    @Test
    void toItemWithBookingsDto() {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        ItemGetResponseDto resultItem = ItemGetResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemGetResponseDto result = itemMapper
                .toItemWithBookingsDto(item, null, null, null);

        assertEquals(resultItem, result);

    }
}