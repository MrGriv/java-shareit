package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(int userId, ItemDto itemDto) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));;
        Item item = mapper.toEntity(itemDto);
        return itemStorage.add(userId, item);
    }

    @Override
    public ItemDto getById(int itemId) {
        return itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item: Вещь с id=" + itemId + " не найдена в списке всех вещей"));
    }

    @Override
    public List<ItemDto> getAllUserItems(int userId) {
        return itemStorage.getAllUserItems(userId);
    }

    @Override
    public List<ItemDto> searchItemsByNameOrDescription(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String lowerCaseText = text.toLowerCase();
        return itemStorage.searchItemsByNameOrDescription(lowerCaseText);
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));;
        Item item = mapper.toEntity(itemDto);
        return itemStorage.update(userId, itemId, item);
    }
}
