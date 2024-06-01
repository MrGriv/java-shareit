package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchState;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingDbStorage bookingStorage;
    private final UserDbStorage userStorage;
    private final ItemDbStorage itemStorage;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDtoOut add(long userId, BookingDtoIn bookingDtoIn) {
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        Item item = itemStorage.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException("Item: Вещь с id=" + bookingDtoIn.getItemId() +
                        " не найдена в списке всех вещей"));
        if (item.getOwnerId().getId() == userId) {
            throw new NotFoundException("Booking: Владелец не может бронировать своб вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item: Вещь недоступна для бронирования");
        }
        if (bookingDtoIn.getStart().isAfter(bookingDtoIn.getEnd())) {
            throw new ValidationException("Booking: Дата начала не может быть позже даты конца бронирования");
        }
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            throw new ValidationException("Booking: Дата окончания не может быть раньше даты начала бронирования");
        }
        if (bookingDtoIn.getEnd().equals(bookingDtoIn.getStart())) {
            throw new ValidationException("Booking: Даты не могут совпадать");
        }
        Booking booking = mapper.toEntity(bookingDtoIn);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toDtoOut(bookingStorage.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking: Бронирование с id=" + bookingId + " не найдено"));
        Item item = booking.getItem();
        if (item.getOwnerId().getId() != userId) {
            throw new NotFoundException("Booking: Пользователь с id=" + userId + " не является владельцем вещи");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return mapper.toDtoOut(bookingStorage.save(booking));
        } else {
            throw new BadRequestException("Booking: Нельзя повторно менять статус");
        }
    }

    @Override
    @Transactional
    public BookingDtoOut getBooking(long userId, long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking: Бронирование с id=" + bookingId + " не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwnerId().getId() != userId) {
            throw new NotFoundException("Booking: Пользователь с id=" + userId +
                    " не является владельцем вещи или автором бронирования");
        }
        return mapper.toDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getAllUserBookings(long userId, String state) {
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));////////////////////
        if (state.equals(SearchState.ALL.name())) {
            return bookingStorage.findAllByBookerOrderByStartDesc(booker).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.FUTURE.name())) {
            return bookingStorage.findAllByBookerAndStartGreaterThanOrderByStartDesc(booker,
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.PAST.name())) {
            return bookingStorage.findAllByBookerAndEndLessThanOrderByStartDesc(booker,
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.CURRENT.name())) {
            return bookingStorage.findAllByBookerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(booker,
                            LocalDateTime.now(),
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.WAITING.name())) {
            return bookingStorage.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING)
                    .stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.REJECTED.name())) {
            return bookingStorage.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED)
                    .stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        }
        throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDtoOut> getAllOwnerBookings(long userId, String state) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        if (itemStorage.findAllByOwnerIdOrderById(owner).isEmpty()) {
            throw new NotFoundException("Item: Пользователь не является владельцем какой либо вещи");
        }
        if (state.equals(SearchState.ALL.name())) {
            return bookingStorage.findAllOwnerBookings(owner.getId()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.FUTURE.name())) {
            return bookingStorage.findAllOwnerFutureBookings(owner.getId(),
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.PAST.name())) {
            return bookingStorage.findAllOwnerPastBookings(owner.getId(),
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.CURRENT.name())) {
            return bookingStorage.findAllOwnerCurrentBookings(owner.getId(),
                            LocalDateTime.now(),
                            LocalDateTime.now()).stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.WAITING.name())) {
            return bookingStorage.findAllOwnerBookingsByStatus(owner.getId(), BookingStatus.WAITING)
                    .stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        } else if (state.equals(SearchState.REJECTED.name())) {
            return bookingStorage.findAllOwnerBookingsByStatus(owner.getId(), BookingStatus.REJECTED)
                    .stream()
                    .map(mapper::toDtoOut)
                    .collect(Collectors.toList());
        }
        throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
    }
}
