package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
@Builder(toBuilder = true)
public class Booking {
    private int id;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
    private String status;
}
