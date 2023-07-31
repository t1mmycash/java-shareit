package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;

    @Test
    void addItem_whenInvokedWithRequestId_thenReturnItemPostDto() {
        ItemPostDto itemPostDto = ItemPostDto.builder()
                .requestId(1L)
                .build();
        User owner = User.builder()
                .id(1L)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .owner(owner)
                .request(itemRequest)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemPostDto(item)).thenReturn(itemPostDto);

        ItemPostDto result = itemService.addItem(itemPostDto, 1L);

        InOrder inOrder = Mockito.inOrder(userRepository, itemRequestRepository, itemRepository);
        assertEquals(itemPostDto, result);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(itemRequestRepository, times(1)).findById(1L);
        inOrder.verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addItem_whenInvokedWithoutRequestId_thenReturnItemPostDto() {
        ItemPostDto itemPostDto = ItemPostDto.builder()
                .build();
        User owner = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .owner(owner)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemPostDto(item)).thenReturn(itemPostDto);

        ItemPostDto result = itemService.addItem(itemPostDto, 1L);

        InOrder inOrder = Mockito.inOrder(userRepository, itemRequestRepository, itemRepository);
        assertEquals(itemPostDto, result);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addItem_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemService.addItem(new ItemPostDto(), 1L));
        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(itemRepository);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void addItem_whenRequestNotFound_thenExceptionWillBeThrown() {
        ItemPostDto itemPostDto = ItemPostDto.builder()
                .requestId(1L)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemRequestNotFoundException e = assertThrows(
                ItemRequestNotFoundException.class, () -> itemService.addItem(itemPostDto, 1L));
        assertEquals("Запроса с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(itemRepository);
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void addComment_whenInvoked_thenReturnCommentResponseDto() {
        User author = new User();
        Item item = new Item();
        Comment comment = Comment.builder()
                .id(0)
                .author(author)
                .item(item)
                .build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .text("text")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRepository.findItemById(anyLong(), any())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndStatus(anyLong(), anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(new Booking()));
        when(commentMapper.toCommentResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto result = itemService.addComment(1L, 1L, comment);

        assertEquals(commentResponseDto, result);
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository, commentRepository);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        inOrder.verify(bookingRepository, times(1)).findFirstByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED);
        inOrder.verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(1L, 1L, new Comment()));
        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addComment_whenItemNotFound_thenExceptionWillBeThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.empty());

        ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(1L, 1L, new Comment()));
        assertEquals("Вещи с id = 1 не существует", e.getMessage());
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void addComment_whenBookingsNotFound_thenExceptionWillBeThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Collections.emptyList());
        UserCannotCommentItemException e = assertThrows(UserCannotCommentItemException.class, () -> itemService.addComment(1L, 1L, new Comment()));
        assertEquals("Пользователь с id = 1 не может комментировать вещь с id = 1, " +
                "т.к. не брал её в аренду", e.getMessage());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void getAllUserItems_whenInvoked_thenReturnItemGetResponseDtoList() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .build();
        BookingInItemDto lastBooking = BookingInItemDto.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(9))
                .build();
        BookingInItemDto nextBooking = BookingInItemDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusHours(12))
                .build();
        List<Comment> comments = List.of(Comment.builder()
                .id(1L)
                .build());
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .build();
        ItemGetResponseDto resultItem = ItemGetResponseDto.builder()
                .id(1L)
                .comments(List.of(commentResponseDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemsByOwnerId(anyLong(), any())).thenReturn(List.of(itemDto));
        when(bookingRepository.findAllItemBookingsByItemId(1L, BookingStatus.APPROVED))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(comments);
        when(commentMapper.toCommentResponseDto(any())).thenReturn(commentResponseDto);
        when(itemMapper.toItemWithBookingsDto(itemDto, lastBooking, nextBooking, List.of(commentResponseDto)))
                .thenReturn(resultItem);

        List<ItemGetResponseDto> result = itemService.getAllUserItems(1, 0, 20);

        assertFalse(result.isEmpty());
        assertEquals(resultItem, result.get(0));
        InOrder inOrder = inOrder(userRepository, itemRepository,
                bookingRepository, commentRepository, commentMapper, itemMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRepository, times(1)).findItemsByOwnerId(1L, PageRequest.of(0, 20));
        inOrder.verify(bookingRepository, times(1)).findAllItemBookingsByItemId(1L, BookingStatus.APPROVED);
        inOrder.verify(commentRepository, times(1)).findAllByItem_Id(1L);
    }

    @Test
    void getAllUserItems_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemService.getAllUserItems(1L, 0, 20));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, itemMapper, commentMapper, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    void getAllUserItems_whenNoItems_thenReturnEmptyList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemsByOwnerId(anyLong(), any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<ItemGetResponseDto> result = itemService.getAllUserItems(1, 0, 20);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).existsById(1L);
        verify(itemRepository, times(1)).findItemsByOwnerId(1L, PageRequest.of(0, 20));
        verifyNoInteractions(itemMapper, commentMapper, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    void getItemResponseDtoById_whenInvokedByOwner_thenReturnItemResponseDto() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).build())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .build();
        BookingInItemDto lastBooking = BookingInItemDto.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(9))
                .build();
        BookingInItemDto nextBooking = BookingInItemDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusHours(12))
                .build();
        List<Comment> comments = List.of(Comment.builder()
                .id(1L)
                .build());
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .build();
        ItemGetResponseDto resultItem = ItemGetResponseDto.builder()
                .id(1L)
                .comments(List.of(commentResponseDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingRepository.findAllItemBookingsByItemId(1L, BookingStatus.APPROVED))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(comments);
        when(commentMapper.toCommentResponseDto(any())).thenReturn(commentResponseDto);
        when(itemMapper.toItemWithBookingsDto(itemDto, lastBooking, nextBooking, List.of(commentResponseDto)))
                .thenReturn(resultItem);

        ItemGetResponseDto result = itemService.getItemResponseDtoById(1L, 1L);

        assertEquals(resultItem, result);
        InOrder inOrder = inOrder(userRepository, itemRepository,
                bookingRepository, commentRepository, commentMapper, itemMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        inOrder.verify(bookingRepository, times(1)).findAllItemBookingsByItemId(1L, BookingStatus.APPROVED);
        inOrder.verify(commentRepository, times(1)).findAllByItem_Id(1L);
    }

    @Test
    void getItemResponseDtoById_whenInvokedNotByOwner_thenReturnItemResponseDto() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(2L).build())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .build();
        List<Comment> comments = List.of(Comment.builder()
                .id(1L)
                .build());
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .build();
        ItemGetResponseDto resultItem = ItemGetResponseDto.builder()
                .id(1L)
                .comments(List.of(commentResponseDto))
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(comments);
        when(commentMapper.toCommentResponseDto(any())).thenReturn(commentResponseDto);
        when(itemMapper.toItemWithBookingsDto(itemDto, null, null, List.of(commentResponseDto)))
                .thenReturn(resultItem);

        ItemGetResponseDto result = itemService.getItemResponseDtoById(1L, 1L);

        assertEquals(resultItem, result);
        InOrder inOrder = inOrder(userRepository, itemRepository,
                bookingRepository, commentRepository, commentMapper, itemMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        inOrder.verify(commentRepository, times(1)).findAllByItem_Id(1L);
        verifyNoInteractions(bookingRepository, itemRequestRepository);
    }


    @Test
    void getItemResponseDtoById_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemService.getItemResponseDtoById(1L, 1L));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, itemMapper, commentMapper, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    void getItemResponseDtoById_whenItemNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.empty());

        ItemNotFoundException e = assertThrows(
                ItemNotFoundException.class, () -> itemService.getItemResponseDtoById(1L, 1L));

        assertEquals("Вещи с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(itemMapper, commentMapper, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    void updateItem_whenInvokedByOwner_thenReturnUpdatedItemDto() {
        User owner = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("name")
                .description("description")
                .available(false)
                .build();
        ItemUpdateDto updates = ItemUpdateDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .build();
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .owner(owner)
                .build();
        ItemDto resultItem = ItemDto.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(resultItem);

        ItemDto result = itemService.updateItem(1L, 1L, updates);

        assertEquals(resultItem, result);
        InOrder inOrder = inOrder(userRepository, itemRepository, itemMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        inOrder.verify(itemRepository, times(1)).save(updatedItem);
        inOrder.verify(itemMapper, times(1)).toItemDto(updatedItem);
        verifyNoMoreInteractions(userRepository, itemMapper, itemRepository);
        verifyNoInteractions(commentMapper, commentRepository, bookingRepository, itemRequestRepository);
    }

    @Test
    void updateItem_whenInvokedNotByOwner_thenReturnUpdatedItemDto() {
        User owner = User.builder()
                .id(2L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("name")
                .description("description")
                .available(false)
                .build();
        ItemUpdateDto updates = ItemUpdateDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));


        AccessDeniedException e = assertThrows(AccessDeniedException.class, () -> itemService.updateItem(1L, 1L, updates));

        assertEquals("У пользователя с id = 1 нет доступа к вещи с id = 1", e.getMessage());
        InOrder inOrder = inOrder(userRepository, itemRepository);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(commentMapper, commentRepository, itemMapper, bookingRepository, itemRequestRepository);
    }

    @Test
    void searchItem_whenInvoked_thenReturnItemDtoList() {
        List<ItemDto> foundItems = List.of(
                ItemDto.builder()
                        .id(1L)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .build());
        when(itemRepository.searchItemsByText("text", PageRequest.of(0, 20)))
                .thenReturn(foundItems);

        List<ItemDto> result = itemService.searchItem("TexT", 0, 20);

        assertEquals(foundItems, result);
        verify(itemRepository, times(1)).searchItemsByText("text", PageRequest.of(0, 20));
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(commentMapper, commentRepository, itemMapper, bookingRepository, itemRequestRepository, userRepository);
    }

    @Test
    void searchItem_whenTextIsBlank_thenExceptionWillBeThrown() {
        List<ItemDto> result = itemService.searchItem(" ", 0, 20);

        assertEquals(Collections.emptyList(), result);
        verifyNoInteractions(itemRepository, commentMapper, commentRepository, itemMapper, bookingRepository, itemRequestRepository, userRepository);
    }

//    @Test
//    void findLastBookingOfItem_whenInvoked_thenReturnLastBooking() {
//        BookingInItemDto lastBooking = BookingInItemDto.builder()
//                .id(1L)
//                .start(LocalDateTime.now().minusDays(10))
//                .end(LocalDateTime.now().minusDays(9))
//                .build();
//        BookingInItemDto nextBooking = BookingInItemDto.builder()
//                .id(2L)
//                .start(LocalDateTime.now().plusHours(10))
//                .end(LocalDateTime.now().plusHours(12))
//                .build();
//        List<BookingInItemDto> bookingsList = List.of(lastBooking, nextBooking);
//
//        BookingInItemDto result = itemService.
//    }


}