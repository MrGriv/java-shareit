package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ItemBooking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long bookerId;
    private BookingStatus status;
}
