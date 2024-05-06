package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFound;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {
    private final ItemMapper itemMapper;
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public ItemDto add(int userId, Item item) {
        item.setId(++id);
        item.setOwner(userId);
        items.put(item.getId(), item);
        return itemMapper.toDto(items.get(item.getId()));
    }

    @Override
    public Optional<ItemDto> getById(int itemId) {
        Item item = items.get(itemId);
        if (item != null) {
            return Optional.of(itemMapper.toDto(item));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ItemDto> getAllUserItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByNameOrDescription(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(int userId, int itemId, Item item) {
        Item updatedItem = items.get(itemId);
        if (updatedItem == null) {
            throw new ItemNotFound("Вещь с id=" + itemId + " не найдена в списке всех вещей");
        }
        if (!(updatedItem.getOwner() == userId)) {
            throw new ItemNotFound("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        }
        updatedItem.setName(item.getName() == null ? updatedItem.getName() : item.getName());
        updatedItem.setDescription(item.getDescription() == null ? updatedItem.getDescription() : item.getDescription());
        updatedItem.setAvailable(item.getAvailable() == null ? updatedItem.getAvailable() : item.getAvailable());
        return itemMapper.toDto(items.put(itemId, updatedItem));
    }
}
