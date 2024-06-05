package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingService.add(userId, bookingDtoIn);
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public BookingDtoOut changeBookingStatus(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long id,
                                             @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, id, approved);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public BookingDtoOut getBooking(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return bookingService.getBooking(userId, id);
    }

    @GetMapping
    public List<BookingDtoOut> getAllUserBookings(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping(ApiPathConstants.OWNER_PATH)
    public List<BookingDtoOut> getAllOwnerBookings(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getAllOwnerBookings(userId, state, from, size);
    }
}
