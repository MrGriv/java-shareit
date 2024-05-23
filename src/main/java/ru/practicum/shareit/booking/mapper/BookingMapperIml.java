package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapperIml implements BookingMapper {
    @Override
    public Booking toEntity(BookingDtoIn bookingDtoIn) {
        return new Booking(bookingDtoIn.getId(), bookingDtoIn.getStart(), bookingDtoIn.getEnd(),
                null, null, bookingDtoIn.getStatus());
    }

    @Override
    public BookingDtoIn toDtoIn(Booking booking) {
        return new BookingDtoIn(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    @Override
    public BookingDtoOut toDtoOut(Booking booking) {
        return new BookingDtoOut(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem(), booking.getBooker(), booking.getStatus());
    }

    @Override
    public ItemBooking toItemBooking(Booking booking) {
        return new ItemBooking(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem(), booking.getBooker().getId(), booking.getStatus());
    }
}
