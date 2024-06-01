package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requestor;
    private Date created;
}
