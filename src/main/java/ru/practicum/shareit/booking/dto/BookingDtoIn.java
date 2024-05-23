package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoIn {
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
