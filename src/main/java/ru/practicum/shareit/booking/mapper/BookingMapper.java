package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {
    Booking toEntity(BookingDtoIn bookingDtoIn);

    BookingDtoIn toDtoIn(Booking booking);

    BookingDtoOut toDtoOut(Booking booking);

    ItemBooking toItemBooking(Booking booking);
}
