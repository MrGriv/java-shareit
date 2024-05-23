package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto commentDto, Item item, User user) {
        return new Comment(commentDto.getId(), commentDto.getText(), item, user, commentDto.getCreated());
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
