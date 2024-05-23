package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStartGreaterThanOrderByStartDesc(User booker, LocalDateTime date);

    List<Booking> findAllByBookerAndEndLessThanOrderByStartDesc(User booker, LocalDateTime date);

    List<Booking> findAllByBookerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(User booker,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    List<Booking> findAllByBookerAndItemAndStatusIsNotAndStartBefore(User user,
                                                                     Item item,
                                                                     BookingStatus status,
                                                                     LocalDateTime start);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllOwnerBookings(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.start > ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllOwnerFutureBookings(Long ownerId, LocalDateTime date);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllOwnerPastBookings(Long ownerId, LocalDateTime date);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.end > ?2 AND b.start < ?3 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllOwnerCurrentBookings(Long ownerId, LocalDateTime date1, LocalDateTime date2);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS it " +
            "WHERE it.ownerId.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllOwnerBookingsByStatus(Long ownerId, BookingStatus status);

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
