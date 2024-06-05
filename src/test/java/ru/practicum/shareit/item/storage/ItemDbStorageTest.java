package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemDbStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemDbStorage itemStorage;

    @Test
    void search() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        user.setId(userQuery.setParameter("name", user.getName()).getSingleResult().getId());
        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                user,
                null);
        Item item1 = new Item(null,
                "УШМ",
                "niceRUB",
                true,
                user,
                null);
        em.persist(item);
        em.persist(item1);
        em.flush();

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);

        PageRequest page = PageRequest.of(0, 20);
        Page<Item> targetItems = itemStorage.search("RuB", page);

        assertThat(targetItems.getContent(), hasSize(items.size()));
        for (Item sourceItem : items) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }
}