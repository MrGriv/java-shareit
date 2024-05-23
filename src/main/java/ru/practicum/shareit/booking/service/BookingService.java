package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut add(long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut changeBookingStatus(long userId, long bookingId, boolean approved);

    BookingDtoOut getBooking(long userId, long bookingId);

    List<BookingDtoOut> getAllUserBookings(long userId, String state);

    List<BookingDtoOut> getAllOwnerBookings(long userId, String state);
}
