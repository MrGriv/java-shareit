package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserHasAlreadyExist;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {
    private final UserMapper userMapper;
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @Override
    public UserDto add(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        return userMapper.toDto(users.get(user.getId()));
    }

    @Override
    public List<UserDto> get() {
        return users.values().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getById(int userId) {
        User user = users.get(userId);
        if (user != null) {
            return Optional.of(userMapper.toDto(user));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public UserDto update(int userId, User user) {
        User updatedUser = users.get(userId);
        if (updatedUser == null) {
            throw new NotFoundException("User: Пользователь с id=" + userId + " не найден");
        }
        user.setId(userId);
        updatedUser.setName(user.getName() == null ? updatedUser.getName() : user.getName());
        if (user.getEmail() != null) {
            List<User> users = get().stream().map(userMapper::toEntity).collect(Collectors.toList());
            for (User userFromStorage : users) {
                if (user.getEmail().equals(userFromStorage.getEmail()) && user.getId() != userFromStorage.getId()) {
                    throw new UserHasAlreadyExist("Пользователь с email=" + user.getEmail() + " уже существует");
                }
            }
        }
        updatedUser.setEmail(user.getEmail() == null ? updatedUser.getEmail() : user.getEmail());
        users.replace(userId, users.get(userId), updatedUser);
        return userMapper.toDto(users.get(userId));
    }

    @Override
    public void delete(int userId) {
        users.remove(userId);
    }
}
