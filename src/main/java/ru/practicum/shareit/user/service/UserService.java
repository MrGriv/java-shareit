package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> get();

    UserDto getById(int userId);

    UserDto update(int userId, UserDto userDto);

    void delete(int userId);
}
