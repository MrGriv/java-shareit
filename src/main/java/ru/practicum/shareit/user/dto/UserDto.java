package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private int id;
    private String name;
    @Email
    @NotNull
    private String email;
}
