package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(int userId, ItemDto itemDto);

    ItemDto getById(int itemId);

    List<ItemDto> getAllUserItems(int userId);

    List<ItemDto> searchItemsByNameOrDescription(String text);

    ItemDto update(int userId, int itemId, ItemDto itemDto);
}
