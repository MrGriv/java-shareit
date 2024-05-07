package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
@Builder(toBuilder = true)
public class BookingDto {
    private int id;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
    private String status;
}
