package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private long userId;
    private ItemPostDto itemPostDto;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .name("User")
                .email("user@mail.ru").build();
        userId = userService.addUser(user).getId();

        itemPostDto = ItemPostDto.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
    }

    @Test
    void addItem() {
        itemService.addItem(itemPostDto, userId);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemSaved = query.setParameter("name", itemPostDto.getName()).getSingleResult();

        assertEquals(itemSaved.getName(), itemPostDto.getName());
        assertEquals(itemSaved.getDescription(), itemPostDto.getDescription());
        assertEquals(itemSaved.getAvailable(), itemPostDto.getAvailable());
        assertNotNull(itemSaved.getOwner());
    }


    @Test
    void updateItem() {
        long itemId = itemService.addItem(itemPostDto, userId).getId();
        ItemUpdateDto itemDtoUpdate = ItemUpdateDto.builder().available(false).build();

        ItemDto itemDtoSaved = itemService.updateItem(userId, itemId, itemDtoUpdate);

        assertEquals(itemDtoSaved.getName(), itemPostDto.getName());
        assertEquals(itemDtoSaved.getDescription(), itemPostDto.getDescription());
        assertTrue(itemDtoUpdate.getAvailable().isPresent());
        assertEquals(itemDtoSaved.getAvailable(), itemDtoUpdate.getAvailable().get());
    }

    @Test
    void getByItemId() {
        long itemId = itemService.addItem(itemPostDto, userId).getId();
        LocalDateTime start = LocalDateTime.now();
        User booker = User.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        long bookerId = userService.addUser(booker).getId();
        BookingDto bookingDtoLast = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        long bookingIdLast = bookingService.addBooking(bookerId, bookingDtoLast).getId();
        BookingDto bookingDtoNext = BookingDto.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        long bookingIdNext = bookingService.addBooking(bookerId, bookingDtoNext).getId();
        bookingService.updateBookingStatus(userId, bookingIdLast, true);
        bookingService.updateBookingStatus(userId, bookingIdNext, true);

        ItemGetResponseDto itemTarget = itemService.getItemResponseDtoById(userId, itemId);

        assertEquals(itemTarget.getName(), itemPostDto.getName());
        assertEquals(itemTarget.getDescription(), itemPostDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemPostDto.getAvailable());

        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void getAllItemsByUserId() {
        long itemId = itemService.addItem(itemPostDto, userId).getId();
        LocalDateTime start = LocalDateTime.now();
        User booker = User.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        long bookerId = userService.addUser(booker).getId();
        BookingDto bookingRequestDtoLast = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        long bookingIdLast = bookingService.addBooking(bookerId, bookingRequestDtoLast).getId();
        BookingDto bookingRequestDtoNext = BookingDto.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        long bookingIdNext = bookingService.addBooking(bookerId, bookingRequestDtoNext).getId();
        bookingService.updateBookingStatus(userId, bookingIdLast, true);
        bookingService.updateBookingStatus(userId, bookingIdNext, true);

        List<ItemGetResponseDto> itemsTarget = itemService.getAllUserItems(userId, 0, 5);
        assertNotNull(itemsTarget);
        assertFalse(itemsTarget.isEmpty());

        ItemGetResponseDto itemTarget = itemsTarget.get(0);

        assertEquals(itemTarget.getName(), itemPostDto.getName());
        assertEquals(itemTarget.getDescription(), itemPostDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemPostDto.getAvailable());

        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void searchItem() {
        ItemPostDto itemDto1 = ItemPostDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true).build();
        itemService.addItem(itemDto1, userId);
        ItemPostDto itemDto2 = ItemPostDto.builder()
                .name("Пила")
                .description("Простая")
                .available(true).build();
        itemService.addItem(itemDto2, userId);

        List<ItemPostDto> itemsExpected = List.of(itemDto1);

        List<ItemDto> targetItems = itemService.searchItem("дрель", 0, 5);

        assertThat(targetItems, hasSize(itemsExpected.size()));
        for (ItemPostDto sourceRequest : itemsExpected) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceRequest.getName())),
                    hasProperty("description", equalTo(sourceRequest.getDescription()))
            )));
        }
    }

    @Test
    void addComment() {
        long itemId = itemService.addItem(itemPostDto, userId).getId();
        User booker = User.builder()
                .name("Booker")
                .email("booker@mail.ru").build();
        long bookerId = userService.addUser(booker).getId();
        BookingDto bookingRequestDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(100)).build();
        long bookingId = bookingService.addBooking(bookerId, bookingRequestDto).getId();
        bookingService.updateBookingStatus(userId, bookingId, true);
        Comment commentDto = Comment.builder().text("text").build();
        itemService.addComment(bookerId, itemId, commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment commentSaved = query.setParameter("text", commentDto.getText()).getSingleResult();

        assertEquals(commentSaved.getText(), commentDto.getText());
        assertEquals(commentSaved.getAuthor().getName(), booker.getName());
        assertNotNull(commentSaved.getItem());
    }
}
