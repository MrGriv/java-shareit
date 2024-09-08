package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestDbStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    Page<ItemRequest> findAllByRequestorNotInOrderByCreatedDesc(List<User> user, Pageable page);
}
