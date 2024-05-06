package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int owner;
    private ItemRequest request;
}
