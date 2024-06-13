package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getAllUserItems(long userId, Integer from, Integer size);

    List<ItemDto> searchItemsByNameOrDescription(String text, Integer from, Integer size);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDto add(long userId, long itemId, CommentDto commentDto);
}
