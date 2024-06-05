package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
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
    public List<ItemDto> getAllUserItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemService.getAllUserItems(userId, from, size);
    }

    @GetMapping(ApiPathConstants.SEARCH_ITEMS_PATH)
    public List<ItemDto> searchItemsByNameOrDescription(@RequestParam String text,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemService.searchItemsByNameOrDescription(text, from, size);
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
