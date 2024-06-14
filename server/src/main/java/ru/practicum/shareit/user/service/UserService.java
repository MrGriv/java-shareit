package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> get();

    UserDto getById(long userId);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);
}
