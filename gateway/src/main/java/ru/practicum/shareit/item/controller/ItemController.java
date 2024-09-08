package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.EmptyList;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        return itemClient.add(userId, itemDto);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable long id,
                                          @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping(ApiPathConstants.SEARCH_ITEMS_PATH)
    public ResponseEntity<Object> searchItemsByNameOrDescription(@RequestParam String text,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "20") Integer size) {
        if (text.isEmpty()) {
            throw new EmptyList();
        }
        return itemClient.searchItemsByNameOrDescription(text, from, size);
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> update(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long id,
                       @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, id, itemDto);
    }

    @PostMapping(ApiPathConstants.ADD_COMMENT_PATH)
    public ResponseEntity<Object> add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @Valid @RequestBody CommentDto commentDto) {
        return itemClient.add(userId, id, commentDto);
    }
}
