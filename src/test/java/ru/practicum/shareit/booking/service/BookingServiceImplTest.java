package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @Test
    void addBooking_whenInvokedByOtherUser_thenReturnBookingResultDto() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResultDto(booking)).thenReturn(bookingResultDto);

        BookingResultDto result = bookingService.addBooking(1L, bookingDto);

        assertEquals(bookingResultDto, result);
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository, bookingMapper);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(itemRepository, times(1)).findItemById(1L, Item.class);
        inOrder.verify(bookingRepository, times(1)).save(booking);
        inOrder.verify(bookingMapper, times(1)).toBookingResultDto(booking);
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenItemNotFound_thenExceptionWillBeThrown() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.empty());

        ItemNotFoundException e = assertThrows(
                ItemNotFoundException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Вещи с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenBookerNotFound_thenExceptionWillBeThrown() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenInvokedByOwner_thenExceptionWillBeThrown() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));

        AccessDeniedException e = assertThrows(
                AccessDeniedException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Пользователь не может забронировать свою же вещь", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenItemNotAvailable_thenExceptionWillBeThrown() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .available(false)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));

        ItemIsNotAvailableException e = assertThrows(
                ItemIsNotAvailableException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Вещь с id = 1 недоступна", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenStartAndEndAreEquals_thenExceptionWillBeThrown() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(now)
                .end(now)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));

        WrongBookingTimeException e = assertThrows(
                WrongBookingTimeException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Время начала и конца бронирования не может совпадать", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void addBooking_whenStartAfterEnd_thenExceptionWillBeThrown() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusHours(5))
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findItemById(1L, Item.class)).thenReturn(Optional.of(item));

        WrongBookingTimeException e = assertThrows(
                WrongBookingTimeException.class, () -> bookingService.addBooking(1L, bookingDto));

        assertEquals("Время конца бронирования не может быть раньше его начала", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemById(1L, Item.class);
        verifyNoMoreInteractions(userRepository, itemRepository);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void updateBookingStatus_whenInvokedWithApproved_thenReturnUpdatedBookingResultDto() {
        Item item = Item.builder()
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Booking approvedBooking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(approvedBooking)).thenReturn(approvedBooking);
        when(bookingMapper.toBookingResultDto(approvedBooking)).thenReturn(bookingResultDto);

        BookingResultDto result = bookingService.updateBookingStatus(1L, 1L, true);

        assertEquals(bookingResultDto, result);
        InOrder inOrder = inOrder(userRepository, bookingRepository, bookingMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(bookingRepository, times(1)).findById(1L);
        inOrder.verify(bookingRepository, times(1)).save(approvedBooking);
        inOrder.verify(bookingMapper, times(1)).toBookingResultDto(approvedBooking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateBookingStatus_whenRejectedWithApproved_thenReturnUpdatedBookingResultDto() {
        Item item = Item.builder()
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Booking rejectedBooking = Booking.builder()
                .id(1L)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(rejectedBooking)).thenReturn(rejectedBooking);
        when(bookingMapper.toBookingResultDto(rejectedBooking)).thenReturn(bookingResultDto);

        BookingResultDto result = bookingService.updateBookingStatus(1L, 1L, false);

        assertEquals(bookingResultDto, result);
        InOrder inOrder = inOrder(userRepository, bookingRepository, bookingMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(bookingRepository, times(1)).findById(1L);
        inOrder.verify(bookingRepository, times(1)).save(rejectedBooking);
        inOrder.verify(bookingMapper, times(1)).toBookingResultDto(rejectedBooking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateBookingStatus_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> bookingService
                        .updateBookingStatus(1L, 1L, false));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, bookingRepository, bookingMapper);
    }

    @Test
    void updateBookingStatus_whenBookingNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        BookingNotFoundException e = assertThrows(
                BookingNotFoundException.class, () -> bookingService
                        .updateBookingStatus(1L, 1L, false));

        assertEquals("Бронирования с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, bookingMapper);
    }

    @Test
    void updateBookingStatus_whenBookingAlreadyApproved_thenExceptionWillBeThrown() {
        Item item = Item.builder()
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingCannotBeChangedException e = assertThrows(
                BookingCannotBeChangedException.class, () -> bookingService
                        .updateBookingStatus(1L, 1L, true));

        assertEquals("Статус брони с id = 1 изменить нельзя", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, bookingRepository);
        verifyNoInteractions(itemRepository, bookingMapper);
    }

    @Test
    void updateBookingStatus_whenUserNotItemOwner_thenExceptionWillBeThrown() {
        Item item = Item.builder()
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        AccessDeniedException e = assertThrows(
                AccessDeniedException.class, () -> bookingService
                        .updateBookingStatus(1L, 1L, true));

        assertEquals("У пользователя с id = 1 нет доступа к бронированию с id = 1", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, bookingRepository);
        verifyNoInteractions(itemRepository, bookingMapper);
    }

    @Test
    void getBooking_whenInvokedByBooker_thenReturnBookingResultDto() {
        User itemOwner = User.builder()
                .id(2L)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(Item.builder()
                        .id(1L)
                        .owner(itemOwner)
                        .build())
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResultDto(booking)).thenReturn(bookingResultDto);

        BookingResultDto result = bookingService.getBooking(1L, 1L);

        assertEquals(bookingResultDto, result);
        InOrder inOrder = inOrder(userRepository, bookingRepository, bookingMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(bookingRepository, times(1)).findById(1L);
        inOrder.verify(bookingMapper, times(1)).toBookingResultDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getBooking_whenInvokedByItemOwner_thenReturnBookingResultDto() {
        User itemOwner = User.builder()
                .id(2L)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(Item.builder()
                        .id(1L)
                        .owner(itemOwner)
                        .build())
                .build();
        BookingResultDto bookingResultDto = BookingResultDto.builder()
                .id(1L)
                .build();
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResultDto(booking)).thenReturn(bookingResultDto);

        BookingResultDto result = bookingService.getBooking(2L, 1L);

        assertEquals(bookingResultDto, result);
        InOrder inOrder = inOrder(userRepository, bookingRepository, bookingMapper);
        inOrder.verify(userRepository, times(1)).existsById(2L);
        inOrder.verify(bookingRepository, times(1)).findById(1L);
        inOrder.verify(bookingMapper, times(1)).toBookingResultDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getBooking_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> bookingService.getBooking(1L, 1L));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository, bookingMapper, itemRepository);
    }

    @Test
    void getBooking_whenBookingNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        BookingNotFoundException e = assertThrows(
                BookingNotFoundException.class, () -> bookingService.getBooking(1L, 1L));

        assertEquals("Бронирования с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, bookingRepository);
        verifyNoInteractions(bookingMapper, itemRepository);
    }

    @Test
    void getBooking_whenInvokedByOtherUser_thenExceptionWillBeThrown() {
        User itemOwner = User.builder()
                .id(2L)
                .build();
        User booker = User.builder()
                .id(1L)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(Item.builder()
                        .id(1L)
                        .owner(itemOwner)
                        .build())
                .build();
        when(userRepository.existsById(3L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        AccessDeniedException e = assertThrows(
                AccessDeniedException.class, () -> bookingService.getBooking(3L, 1L));

        assertEquals("У пользователя с id = 3 нет доступа к бронированию с id = 1", e.getMessage());
        verify(userRepository, times(1)).existsById(3L);
        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, bookingRepository);
        verifyNoInteractions(itemRepository, bookingMapper);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypeAll_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllBookingsByBookerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "ALL", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findAllBookingsByBookerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypePast_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findPastBookingsByBookerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "PAST", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findPastBookingsByBookerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypeCurrent_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentBookingsByBookerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "CURRENT", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findCurrentBookingsByBookerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypeFuture_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findFutureBookingsByBookerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "FUTURE", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findFutureBookingsByBookerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypeWaiting_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllBookingsByBookerIdAndStatus(
                1L, BookingStatus.WAITING, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "WAITING", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findAllBookingsByBookerIdAndStatus(
                1L, BookingStatus.WAITING, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvokedWithSortTypeRejected_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllBookingsByBookerIdAndStatus(
                1L, BookingStatus.REJECTED, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService.getAllUserBookings(1L, "REJECTED", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findAllBookingsByBookerIdAndStatus(
                1L, BookingStatus.REJECTED, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookings_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> bookingService
                        .getAllUserBookings(1L, "ALL", 0, 20));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository, bookingMapper, itemRepository);
    }

    @Test
    void getAllUserBookings_whenInvalidSortType_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);

        InvalidSortTypeException e = assertThrows(
                InvalidSortTypeException.class, () -> bookingService
                        .getAllUserBookings(1L, "INVALID", 0, 20));

        assertEquals("Unknown state: INVALID", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository, bookingMapper, itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypeAll_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "ALL", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findAllUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypePast_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findPastUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "PAST", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findPastUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypeCurrent_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "CURRENT", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findCurrentUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypeFuture_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findFutureUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "FUTURE", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findFutureUserItemsBookingsByOwnerId(1L, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypeWaiting_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllUserItemsBookingsByOwnerIdAndStatus(
                1L, BookingStatus.WAITING, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "WAITING", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1))
                .findAllUserItemsBookingsByOwnerIdAndStatus(
                        1L, BookingStatus.WAITING, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvokedWithSortTypeRejected_thenReturnBookingResultDtoList() {
        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1L)
                        .build(),
                Booking.builder()
                        .id(2L)
                        .build());
        List<BookingResultDto> mappedBookings = List.of(
                BookingResultDto.builder()
                        .id(1L)
                        .build(),
                BookingResultDto.builder()
                        .id(2L)
                        .build());
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllUserItemsBookingsByOwnerIdAndStatus(
                1L, BookingStatus.REJECTED, PageRequest.of(0, 20, sort)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingResultDtoList(bookings)).thenReturn(mappedBookings);

        List<BookingResultDto> result = bookingService
                .getAllUserBookedItemsBookings(1L, "REJECTED", 0, 20);

        assertEquals(mappedBookings, result);
        verify(userRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).findAllUserItemsBookingsByOwnerIdAndStatus(
                1L, BookingStatus.REJECTED, PageRequest.of(0, 20, sort));
        verify(bookingMapper, times(1)).toBookingResultDtoList(bookings);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> bookingService
                        .getAllUserBookedItemsBookings(1L, "ALL", 0, 20));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository, bookingMapper, itemRepository);
    }

    @Test
    void getAllUserBookedItemsBookings_whenInvalidSortType_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);

        InvalidSortTypeException e = assertThrows(
                InvalidSortTypeException.class, () -> bookingService
                        .getAllUserBookedItemsBookings(1L, "INVALID", 0, 20));

        assertEquals("Unknown state: INVALID", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository, bookingMapper, itemRepository);
    }
}