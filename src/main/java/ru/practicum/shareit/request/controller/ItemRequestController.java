package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping(ApiPathConstants.ALL_PATH)
    public List<ItemRequestDto> getAllRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ItemRequestDto getById(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemRequestService.getById(userId, id);
    }
}
