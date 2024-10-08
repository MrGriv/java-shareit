package ru.practicum.shareit.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.ErrorResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.EmptyList;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> handleEmptyList(final EmptyList e) {
        log.debug("Получен статус 200 OK {}", e.getMessage(), e);
        return new ArrayList<>();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.debug("Получен статус 400 CONFLICT {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
