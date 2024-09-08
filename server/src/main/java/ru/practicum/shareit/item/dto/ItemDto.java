package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBooking lastBooking;
    private ItemBooking nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
