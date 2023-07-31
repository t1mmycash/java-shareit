package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.SortType;
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
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResultDto addBooking(long bookerId, BookingDto booking) {
        User user = getUserById(bookerId);
        Item item = getItemById(booking.getItemId());
        if (bookerId == item.getOwner().getId()) {
            throw new AccessDeniedException("Пользователь не может забронировать свою же вещь");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException(String.format("Вещь с id = %d недоступна", item.getId()));
        }
        return bookingMapper.toBookingResultDto(bookingRepository.save(Booking.builder()
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
        return bookingMapper.toBookingResultDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResultDto getBooking(long userId, long bookingId) {
        userExistenceCheck(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирования с id = %d не существует", bookingId)));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(
                    String.format("У пользователя с id = %d нет доступа к бронированию с id = %d", userId, bookingId));
        }
        return bookingMapper.toBookingResultDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResultDto> getAllUserBookings(long userId, String sort, int from, int size) {
        userExistenceCheck(userId);
        PageRequest pageRequest = createPageRequest(from, size);
        switch (SortType.valueOf(sort)) {
            case ALL:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllBookingsByBookerId(userId, pageRequest));
            case PAST:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findPastBookingsByBookerId(userId, pageRequest));
            case CURRENT:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findCurrentBookingsByBookerId(userId, pageRequest));
            case FUTURE:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findFutureBookingsByBookerId(userId, pageRequest));
            case WAITING:
            case REJECTED:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllBookingsByBookerIdAndStatus(
                                userId, BookingStatus.valueOf(sort), pageRequest));
            default:
                throw new InvalidSortTypeException(String.format("Unknown state: %s", sort));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResultDto> getAllUserBookedItemsBookings(long ownerId, String sort, int from, int size) {
        userExistenceCheck(ownerId);
        PageRequest pageRequest = createPageRequest(from, size);
        switch (SortType.valueOf(sort)) {
            case ALL:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllUserItemsBookingsByOwnerId(ownerId, pageRequest));
            case PAST:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findPastUserItemsBookingsByOwnerId(ownerId, pageRequest));
            case CURRENT:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findCurrentUserItemsBookingsByOwnerId(ownerId, pageRequest));
            case FUTURE:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findFutureUserItemsBookingsByOwnerId(ownerId, pageRequest));
            case WAITING:
            case REJECTED:
                return bookingMapper.toBookingResultDtoList(
                        bookingRepository.findAllUserItemsBookingsByOwnerIdAndStatus(
                                ownerId, BookingStatus.valueOf(sort), pageRequest));
            default:
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

    private PageRequest createPageRequest(int from, int size) {
        PageRequest pageRequest;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (from == 0) {
            pageRequest = PageRequest.of(from, size, sort);
        } else {
            pageRequest = PageRequest.of(from / size, size, sort);
        }
        return pageRequest;
    }

}
