package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerOrderByStartDesc(User user, Pageable page);

    Page<Booking> findAllByBookerAndStartGreaterThanOrderByStartDesc(User booker, LocalDateTime date, Pageable page);

    Page<Booking> findAllByBookerAndEndLessThanOrderByStartDesc(User booker, LocalDateTime date, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.booker AS u " +
            "WHERE u.id = ?1 AND b.start <= ?2 AND b.end >= ?3 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllCurrentUserBookings(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerAndItemAndStatusIsNotAndStartBefore(User user,
                                                                     Item item,
                                                                     BookingStatus status,
                                                                     LocalDateTime start);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllOwnerBookings(Long ownerId, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.start > ?2 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllOwnerFutureBookings(Long ownerId, LocalDateTime date, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllOwnerPastBookings(Long ownerId, LocalDateTime date, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.end > ?2 AND b.start < ?3 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllOwnerCurrentBookings(Long ownerId, LocalDateTime date1, LocalDateTime date2, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC ")
    Page<Booking> findAllOwnerBookingsByStatus(Long ownerId, BookingStatus status, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.start > ?2 AND it.id = ?3 AND b.status != ?4 " +
            "ORDER BY b.start ")
    List<Booking> findItemFutureBooking(Long ownerId, LocalDateTime date, Long itemId, BookingStatus status);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.start <= ?2 AND it.id = ?3 AND b.status != ?4 " +
            "ORDER BY b.end DESC ")
    List<Booking> findItemPastBooking(Long ownerId, LocalDateTime date, Long itemId, BookingStatus status);
}
