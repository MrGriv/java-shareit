package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getAllUserItems(long userId);

    List<ItemDto> searchItemsByNameOrDescription(String text);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDto add(long userId, long itemId, CommentDto commentDto);
}
