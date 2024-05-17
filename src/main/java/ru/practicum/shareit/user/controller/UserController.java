package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public UserDto getById(@PathVariable int id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> get() {
        return userService.get();
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public UserDto update(@RequestBody UserDto userDto, @PathVariable int id) {
        return userService.update(id, userDto);
    }

    @DeleteMapping(ApiPathConstants.BY_ID_PATH)
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }
}
