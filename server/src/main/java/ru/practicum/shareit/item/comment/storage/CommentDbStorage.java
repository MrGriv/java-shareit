package ru.practicum.shareit.item.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentDbStorage extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem(Item item);
}
