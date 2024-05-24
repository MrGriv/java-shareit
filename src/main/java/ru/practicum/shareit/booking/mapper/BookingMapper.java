package ru.practicum.shareit.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    Booking toEntity(BookingDtoIn bookingDtoIn);

    BookingDtoOut toDtoOut(Booking booking);

    @Mapping(source = "booking.booker.id", target = "bookerId")
    ItemBooking toItemBooking(Booking booking);
}
