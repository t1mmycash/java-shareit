package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResultDto addBooking(long bookerId, BookingDto booking) {
        User user = getUserById(bookerId);
        Item item = getItemById(booking.getItemId());
        if (bookerId == item.getOwner().getId()) {
            throw new AccessDeniedException("Пользователь не может забронировать свою же вещь");
        }
        if (!item.isAvailable()) {
            throw new ItemIsNotAvailableException(String.format("Вещь с id = %d недоступна", item.getId()));
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new WrongBookingTimeException("Время начала и конца бронирования не может совпадать");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new WrongBookingTimeException("Время конца бронирования не может быть раньше его начала");
        }
        return BookingMapper.toBookingResultDto(bookingRepository.save(Booking.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build()
        ));
    }

    @Override
    public BookingResultDto updateBookingStatus(long ownerId, long bookingId, boolean approved) {
        userExistenceCheck(ownerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирования с id = %d не существует", bookingId)));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingCannotBeChangedException(String.format("Статус брони с id = %d изменить нельзя", bookingId));
        }
        if (ownerId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(
                    String.format("У пользователя с id = %d нет доступа к бронированию с id = %d", ownerId, bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingResultDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResultDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирования с id = %d не существует", bookingId)));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(
                    String.format("У пользователя с id = %d нет доступа к бронированию с id = %d", userId, bookingId));
        }
        return BookingMapper.toBookingResultDto(booking);
    }

    @Override
    public List<BookingResultDto> getAllUserBookings(long userId, String sort) {
        userExistenceCheck(userId);
        sortValidNameCheck(sort);
        switch (Sort.valueOf(sort)) {
            case ALL:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllBookingsByBookerId(userId));
            case PAST:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findPastBookingsByBookerId(userId));
            case CURRENT:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findCurrentBookingsByBookerId(userId));
            case FUTURE:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findFutureBookingsByBookerId(userId));
            case WAITING:
            case REJECTED:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllBookingsByBookerIdAndStatus(userId, BookingStatus.valueOf(sort)));
            default:
                throw new InvalidSortTypeException(String.format("Unknown state: %s", sort));
        }
    }

    @Override
    public List<BookingResultDto> getAllUserBookedItemsBookings(long ownerId, String sort) {
        userExistenceCheck(ownerId);
        sortValidNameCheck(sort);
        switch (Sort.valueOf(sort)) {
            case ALL:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllUserItemsBookingsByOwnerId(ownerId));
            case PAST:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findPastUserItemsBookingsByOwnerId(ownerId));
            case CURRENT:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findCurrentUserItemsBookingsByOwnerId(ownerId));
            case FUTURE:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findFutureUserItemsBookingsByOwnerId(ownerId));
            case WAITING:
            case REJECTED:
                return BookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllUserItemsBookingsByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(sort)));
            default:
                throw new InvalidSortTypeException(String.format("Unknown state: %s", sort));
        }
    }

    private void sortValidNameCheck(String sort) {
        try {
            Sort.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new InvalidSortTypeException(String.format("Unknown state: %s", sort));
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findItemById(itemId, Item.class).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещи с id = %d не существует", itemId)));
    }

    private void userExistenceCheck(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id = %d не существует", userId));
        }
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("Пользователя с id = %d не существует", userId)));
    }

}
