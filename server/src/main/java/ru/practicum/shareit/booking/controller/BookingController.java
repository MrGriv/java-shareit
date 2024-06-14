package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.ApiPathConstants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingService.add(userId, bookingDtoIn);
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public BookingDtoOut changeBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long id,
                                             boolean approved) {
        return bookingService.changeBookingStatus(userId, id, approved);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public BookingDtoOut getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return bookingService.getBooking(userId, id);
    }

    @GetMapping
    public List<BookingDtoOut> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping(ApiPathConstants.OWNER_PATH)
    public List<BookingDtoOut> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getAllOwnerBookings(userId, state, from, size);
    }
}
