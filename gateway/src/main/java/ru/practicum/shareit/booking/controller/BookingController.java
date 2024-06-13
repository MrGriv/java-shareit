package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ApiPathConstants;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllUserBookings(userId, state, from, size);
	}

	@PatchMapping(ApiPathConstants.BY_ID_PATH)
	public ResponseEntity<Object> changeBookingStatus(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long id,
											 @RequestParam boolean approved) {
		log.info("Change Booking Status userId={}, bookingId={}, approved={}", userId, id, approved);
		return bookingClient.changeBookingStatus(userId, id, approved);
	}

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.add(userId, requestDto);
	}

	@GetMapping(ApiPathConstants.BY_ID_PATH)
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long id) {
		log.info("Get booking {}, userId={}", id, userId);
		return bookingClient.getBooking(userId, id);
	}

	@GetMapping(ApiPathConstants.OWNER_PATH)
	public ResponseEntity<Object> getAllOwnerBookings(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
												   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
												   @Positive @RequestParam(defaultValue = "20") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllOwnerBookings(userId, state, from, size);
	}
}
