package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping(ApiPathConstants.ALL_PATH)
    public ResponseEntity<Object> getAllRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> getById(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long id) {
        return itemRequestClient.getById(userId, id);
    }
}
