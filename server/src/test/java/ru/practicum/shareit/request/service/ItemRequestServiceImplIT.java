package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIT {
    private final EntityManager em;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;
    private long userId;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .name("User")
                .email("user@mail.ru").build();
        userId = userService.addUser(user).getId();

        itemRequest = ItemRequest.builder().description("description").build();
    }

    @SneakyThrows
    @Test
    void addRequest() {
        Long itemRequestId = itemRequestService.addItemRequest(userId, itemRequest).getId();
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequestSaved = query.setParameter("id", itemRequestId).getSingleResult();

        assertEquals(itemRequestSaved.getDescription(), itemRequest.getDescription());
        assertNotNull(itemRequestSaved.getRequester());
        assertNotNull(itemRequestSaved.getCreated());
    }

    @Test
    void getUserRequests() {
        List<ItemRequest> sourceRequests = new ArrayList<>(List.of(
                ItemRequest.builder().description("desc1").build(),
                ItemRequest.builder().description("desc2").build(),
                ItemRequest.builder().description("desc3").build()));
        User userNew = User.builder()
                .name("UserNew")
                .email("userNew@mail.ru").build();
        userId = userService.addUser(userNew).getId();
        for (ItemRequest request : sourceRequests) {
            itemRequestService.addItemRequest(userId, request);
        }

        List<ItemRequestDto> targetRequests = itemRequestService.getUserRequests(userId);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequest sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getRequestById() {
        long itemRequestId = itemRequestService.addItemRequest(userId, itemRequest).getId();

        ItemRequestDto targetItemRequest = itemRequestService.getRequestById(userId, itemRequestId);

        assertEquals(targetItemRequest.getDescription(), itemRequest.getDescription());
        assertNotNull(targetItemRequest.getCreated());
    }

    @Test
    void getAllRequests() {
        List<ItemRequest> sourceRequests = new ArrayList<>(List.of(
                ItemRequest.builder().description("desc1").build(),
                ItemRequest.builder().description("desc2").build(),
                ItemRequest.builder().description("desc3").build()));
        User userNew = User.builder()
                .name("UserNew")
                .email("userNew@mail.ru").build();
        long userId2 = userService.addUser(userNew).getId();
        for (ItemRequest request : sourceRequests) {
            itemRequestService.addItemRequest(userId2, request);
        }

        List<ItemRequestDto> targetRequests = itemRequestService.getAllRequests(userId, 0, 5);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequest sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getAllRequests_testPaging() {
        List<ItemRequest> sourceRequests = new ArrayList<>(List.of(
                ItemRequest.builder().description("desc1").build(),
                ItemRequest.builder().description("desc2").build(),
                ItemRequest.builder().description("desc3").build()));
        User userNew = User.builder()
                .name("UserNew")
                .email("userNew@mail.ru").build();
        long userId2 = userService.addUser(userNew).getId();
        for (ItemRequest request : sourceRequests) {
            itemRequestService.addItemRequest(userId2, request);
        }
        int from = 0;
        int size = 2;
        List<ItemRequestDto> targetRequests = itemRequestService.getAllRequests(userId, from, size);

        assertThat(targetRequests, hasSize(size));

        from = 2;

        targetRequests = itemRequestService.getAllRequests(userId, from, size);

        assertThat(targetRequests, hasSize(1));
    }
}