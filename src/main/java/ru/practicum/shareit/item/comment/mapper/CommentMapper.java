package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {
    @Mapping(source = "comment.author.name", target = "authorName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "user", target = "author")
    @Mapping(source = "commentDto.id", target = "id")
    Comment toEntity(CommentDto commentDto, Item item, User user);
}
