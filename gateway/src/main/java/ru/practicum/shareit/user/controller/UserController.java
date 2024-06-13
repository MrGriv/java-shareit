package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ApiPathConstants;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto userDto) {
        return userClient.add(userDto);
    }

    @GetMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable int id) {
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> get() {
        return userClient.get();
    }

    @PatchMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable long id) {
        return userClient.update(id, userDto);
    }

    @DeleteMapping(ApiPathConstants.BY_ID_PATH)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return userClient.delete(id);
    }
}
