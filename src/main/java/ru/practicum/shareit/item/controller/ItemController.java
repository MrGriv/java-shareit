package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ItemDto getById(@PathVariable long id, @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping(ApiPathConstants.SEARCH_ITEMS_PATH)
    public List<ItemDto> searchItemsByNameOrDescription(@RequestParam String text) {
        return itemService.searchItemsByNameOrDescription(text);
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public ItemDto update(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long id,
                       @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @PostMapping(ApiPathConstants.ADD_COMMENT_PATH)
    public CommentDto add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @Valid @RequestBody CommentDto commentDto) {
        return itemService.add(userId, id, commentDto);
    }
}
