package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.booker.id = ?1")
    List<Booking> findAllBookingsByBookerId(long bookerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.booker.id = ?1 and b.end < current_timestamp")
    List<Booking> findPastBookingsByBookerId(long bookerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.booker.id = ?1 and b.start < current_timestamp and b.end > current_timestamp")
    List<Booking> findCurrentBookingsByBookerId(long bookerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.booker.id = ?1 and b.start > current_timestamp")
    List<Booking> findFutureBookingsByBookerId(long bookerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.booker.id = ?1 and b.status = ?2")
    List<Booking> findAllBookingsByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.item.owner.id = ?1")
    List<Booking> findAllUserItemsBookingsByOwnerId(long ownerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.item.owner.id = ?1 and b.end < current_timestamp")
    List<Booking> findPastUserItemsBookingsByOwnerId(long ownerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.item.owner.id = ?1 and b.start < CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP")
    List<Booking> findCurrentUserItemsBookingsByOwnerId(long ownerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.item.owner.id = ?1 and b.start > current_timestamp")
    List<Booking> findFutureUserItemsBookingsByOwnerId(long ownerId, Pageable pageRequest);

    @Query("select b from Booking as b join b.booker join b.item " +
            "where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllUserItemsBookingsByOwnerIdAndStatus(long ownerId, BookingStatus status, Pageable pageRequest);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInItemDto(b.id, b.start, b.end, b.status, b.booker.id) from Booking as b " +
            "where b.item.id = ?1 and b.status = ?2")
    List<BookingInItemDto> findAllItemBookingsByItemId(long itemId, BookingStatus status);

    @Query("select b from Booking as b " +
            "where b.item.id = ?1 and b.booker.id = ?2 and b.status = ?3 and b.end < current_timestamp")
    List<Booking> findFirstByItem_IdAndBooker_IdAndStatus(long itemId, long userId, BookingStatus status);

}
