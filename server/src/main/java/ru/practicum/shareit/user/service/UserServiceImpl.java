package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserDbStorage userStorage;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userStorage.save(user));
    }

    @Override
    public List<UserDto> get() {
        return userStorage.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto getById(long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User oldUser = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        User user = userMapper.toEntity(userDto);
        user.setId(userId);
        user.setName(user.getName() == null ? oldUser.getName() : user.getName());
        user.setEmail(user.getEmail() == null ? oldUser.getEmail() : user.getEmail());
        return userMapper.toDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userStorage.deleteById(userId);
    }
}
