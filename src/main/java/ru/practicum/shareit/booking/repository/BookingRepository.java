package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.booker.id = ?1 order by b.start desc ")
    List<Booking> findAllBookingsByBookerId(long bookerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.booker.id = ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> findPastBookingsByBookerId(long bookerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.booker.id = ?1 and b.start < current_timestamp and b.end > current_timestamp order by b.start desc")
    List<Booking> findCurrentBookingsByBookerId(long bookerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.booker.id = ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findFutureBookingsByBookerId(long bookerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.booker.id = ?1 and b.status = ?2 order by b.start desc ")
    List<Booking> findAllBookingsByBookerIdAndStatus(long bookerId, BookingStatus status);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.item.owner.id = ?1 order by b.start desc ")
    List<Booking> findAllUserItemsBookingsByOwnerId(long ownerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.item.owner.id = ?1 and b.end < current_timestamp order by b.start desc ")
    List<Booking> findPastUserItemsBookingsByOwnerId(long ownerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.item.owner.id = ?1 and b.start < current_timestamp and b.end > current_timestamp order by b.start desc ")
    List<Booking> findCurrentUserItemsBookingsByOwnerId(long ownerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.item.owner.id = ?1 and b.start > current_timestamp order by b.start desc ")
    List<Booking> findFutureUserItemsBookingsByOwnerId(long ownerId);

    @Query("select b from Booking as b join fetch b.booker join fetch b.item " +
            "where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc ")
    List<Booking> findAllUserItemsBookingsByOwnerIdAndStatus(long ownerId, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInItemDto(b.id, b.start, b.end, b.status, b.booker.id) from Booking as b " +
            "where b.item.id = ?1 and b.status = ?2")
    List<BookingInItemDto> findAllItemBookingsByItemId(long itemId, BookingStatus status);

    @Query("select b from Booking as b " +
            "where b.item.id = ?1 and b.booker.id = ?2 and b.status = ?3 and b.end < current_timestamp ")
    List<Booking> findFirstByItem_IdAndBooker_IdAndStatus(long itemId, long userId, BookingStatus status);

}
