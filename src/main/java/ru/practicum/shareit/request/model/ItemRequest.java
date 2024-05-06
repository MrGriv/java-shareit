package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder(toBuilder = true)
public class ItemRequest {
    private int id;
    private String description;
    private User requestor;
    private Date created;
}
