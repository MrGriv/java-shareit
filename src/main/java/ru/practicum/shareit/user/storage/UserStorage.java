package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    UserDto add(User user);

    List<UserDto> get();

    Optional<UserDto> getById(int userId);

    UserDto update(int userId, User user);

    void delete(int userId);
}
