package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDbStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(User user);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) and i.available = true" +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text);
}
