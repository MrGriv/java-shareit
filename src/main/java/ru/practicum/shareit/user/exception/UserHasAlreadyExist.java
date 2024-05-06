package ru.practicum.shareit.user.exception;

public class UserHasAlreadyExist extends RuntimeException {
    public UserHasAlreadyExist(String msg) {
        super(msg);
    }
}
