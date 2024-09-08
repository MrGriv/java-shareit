package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ApiPathConstants;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ItemDto getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "20") Integer size) {
        return itemService.getAllUserItems(userId, from, size);
    }

    @GetMapping(ApiPathConstants.SEARCH_ITEMS_PATH)
    public List<ItemDto> searchItemsByNameOrDescription(@RequestParam String text,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "20") Integer size) {
        return itemService.searchItemsByNameOrDescription(text, from, size);
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long id,
                       @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @PostMapping(ApiPathConstants.ADD_COMMENT_PATH)
    public CommentDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody CommentDto commentDto) {
        return itemService.add(userId, id, commentDto);
    }
}
