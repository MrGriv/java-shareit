package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private ItemBooking lastBooking;
    private ItemBooking nextBooking;
    private List<CommentDto> comments;
}
