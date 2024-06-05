package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserDbStorage userStorage;
    private final ItemDbStorage itemStorage;
    private final ItemRequestDbStorage itemRequestStorage;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto add(long userId, ItemRequestDto itemRequestDto) {
        User requestor = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.toDto(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        User requestor = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequestorOrderByCreatedDesc(requestor);
        return getItemRequestDtoList(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequestorNotInOrderByCreatedDesc(
                Collections.singletonList(user), page).getContent();
        return getItemRequestDtoList(itemRequests);
    }

    @Override
    @Transactional
    public ItemRequestDto getById(long userId, long requestId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User: Пользователь с id=" + userId + " не найден"));
        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest: Запрос с id=" + requestId + " не найден"));
        return getItemRequestDtoList(Collections.singletonList(request)).get(0);
    }

    private List<ItemRequestDto> getItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> requestDtoList = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            List<ItemShort> items = itemStorage.findAllByRequestId(request.getId());
            ItemRequestDto itemRequestDto = itemRequestMapper.toDto(request);
            itemRequestDto.setItems(items.isEmpty() ? new ArrayList<>() : items);
            requestDtoList.add(itemRequestDto);
        }
        return requestDtoList;
    }
}
