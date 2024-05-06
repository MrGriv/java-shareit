package ru.practicum.shareit.user.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String msg) {
        super(msg);
    }
}
