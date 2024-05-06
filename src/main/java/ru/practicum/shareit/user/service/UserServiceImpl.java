package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserHasAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFound;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        List<User> users = get().stream().map(userMapper::toEntity).collect(Collectors.toList());
        for (User userFromStorage : users) {
            if (user.getEmail().equals(userFromStorage.getEmail())) {
                throw new UserHasAlreadyExist("Пользователь с email="+ user.getEmail() +" уже существует");
            }
        }
        return userStorage.add(user);
    }

    @Override
    public List<UserDto> get() {
        return userStorage.get();
    }

    @Override
    public UserDto getById(int userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> new UserNotFound("Пользователь с id=" + userId + " не найден"));
    }

    @Override
    public UserDto update(int userId, UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userStorage.update(userId, user);
    }

    @Override
    public void delete(int userId) {
        userStorage.delete(userId);
    }
}
