package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
