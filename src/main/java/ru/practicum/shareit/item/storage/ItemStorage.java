package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    ItemDto add(int userId, Item item);

    Optional<ItemDto> getById(int itemId);

    List<ItemDto> getAllUserItems(int userId);

    List<ItemDto> searchItemsByNameOrDescription(String text);

    ItemDto update(int userId, int itemId, Item item);
}
