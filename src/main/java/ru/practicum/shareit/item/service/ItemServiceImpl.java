package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentDbStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final ItemDbStorage itemStorage;
    private final UserDbStorage userStorage;
    private final BookingDbStorage bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentDbStorage commentStorage;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        Item item = mapper.toEntity(itemDto);
        item.setOwnerId(user);
        return mapper.toDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto getById(long itemId, long userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item: Вещь с id=" + itemId +
                        " не найдена в списке всех вещей"));
        if (item.getOwnerId().getId() == userId) {
            return addBookingsAndComments(item, userId);
        }
        return addComments(item);
    }

    @Override
    public List<ItemDto> getAllUserItems(long userId, Integer from, Integer size) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemStorage.findAllByOwnerIdOrderById(owner, page)
                .map((Item item) -> addBookingsAndComments(item, userId))
                .getContent();
    }

    private ItemDto addComments(Item item) {
        List<CommentDto> itemComments = commentStorage.findAllByItem(item).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        ItemDto itemDto = mapper.toDto(item);
        itemDto.setComments(itemComments.isEmpty() ? new ArrayList<>() : itemComments);
        return itemDto;
    }

    private ItemDto addBookingsAndComments(Item item, Long userId) {
        ItemDto itemDto = addComments(item);
        List<Booking> itemFutureBooking = bookingStorage.findItemFutureBooking(
                userId,
                LocalDateTime.now(),
                item.getId(),
                BookingStatus.REJECTED);
        List<Booking> itemPastBooking = bookingStorage.findItemPastBooking(
                userId,
                LocalDateTime.now(),
                item.getId(),
                BookingStatus.REJECTED);
        if (!itemFutureBooking.isEmpty()) {
            itemDto.setNextBooking(bookingMapper.toItemBooking(itemFutureBooking.get(0)));
        }
        if (!itemPastBooking.isEmpty()) {
            itemDto.setLastBooking(bookingMapper.toItemBooking(itemPastBooking.get(0)));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItemsByNameOrDescription(String text, Integer from, Integer size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemStorage.search(text, page)
                .map(mapper::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        Item updatedItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item: Вещь с id=" + itemId +
                        " не найдена в списке всех вещей"));
        Item item = mapper.toEntity(itemDto);
        updatedItem.setName(item.getName() == null ? updatedItem.getName() : item.getName());
        updatedItem.setDescription(item.getDescription() == null ? updatedItem.getDescription() : item.getDescription());
        updatedItem.setAvailable(item.getAvailable() == null ? updatedItem.getAvailable() : item.getAvailable());
        return mapper.toDto(itemStorage.save(updatedItem));
    }

    @Override
    @Transactional
    public CommentDto add(long userId,long itemId, CommentDto commentDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item: Вещь с id=" + itemId +
                        " не найдена в списке всех вещей"));
        List<Booking> userBookings = bookingStorage.findAllByBookerAndItemAndStatusIsNotAndStartBefore(user,
                item,
                BookingStatus.REJECTED,
                LocalDateTime.now());
        if (!userBookings.isEmpty()) {
            Comment comment = commentMapper.toEntity(commentDto, item, user);
            comment.setCreated(LocalDateTime.now());
            return commentMapper.toDto(commentStorage.save(comment));
        }
        throw new BadRequestException("Comment: Пользователь не может добавить комментарий к вещи," +
                " у которой он не был владельцем");
    }
}
