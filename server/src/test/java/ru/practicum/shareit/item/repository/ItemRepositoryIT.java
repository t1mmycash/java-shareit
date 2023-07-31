package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper = new ItemMapper();

    private long userId;
    private long requestId;
    private Item item;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .name("User")
                .email("user@mail.ru").build();
        userRepository.save(user);
        userId = user.getId();
        ItemRequest request = ItemRequest.builder()
                .description("desc")
                .created(LocalDateTime.now())
                .requester(user)
                .build();
        requestRepository.save(request);
        requestId = request.getId();
        item = Item.builder()
                .name("Saw")
                .description("Desc")
                .available(true)
                .request(request)
                .owner(user).build();
        item = itemRepository.save(item);
    }

    @Test
    void findItemsByOwnerId() {
        PageRequest page = PageRequest.of(0, 1);
        List<ItemDto> actualItems = itemRepository.findItemsByOwnerId(userId, page);

        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());
        assertEquals(itemMapper.toItemDto(item), actualItems.get(0));
    }

    @Test
    void searchItemsByText() {
        PageRequest page = PageRequest.of(0, 1);
        String text = "esc";

        List<ItemDto> actualItems = itemRepository.searchItemsByText(text, page);

        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());
        assertEquals(itemMapper.toItemDto(item), actualItems.get(0));
    }

    @Test
    void getAllResponsesByRequestId() {
        List<ItemInItemRequestDto> result = itemRepository.getAllResponsesByRequestId(requestId);

        assertEquals(result.size(), 1);
        assertEquals("Saw", result.get(0).getName());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }
}