package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemService itemService;
    private final EntityManager em;

    @Test
    void add() {
        ItemDto itemDto = new ItemDto(null,
                "rubanok",
                "cool",
                true,
                null,
                null,
                null,
                null);
        User user = new User(null, "Ivan", "iv@mail.ru");

        em.persist(user);
        em.flush();
        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);
        itemService.add(userQuery.setParameter("name", user.getName()).getSingleResult().getId(), itemDto);

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);
        Item item = itemQuery.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getById() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();
        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        user.setId(userQuery.setParameter("name", user.getName()).getSingleResult().getId());
        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                user,
                null);
        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);
        ItemDto itemDto = itemService.getById(
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                userQuery.setParameter("name", user.getName()).getSingleResult().getId());

        assertThat(itemDto, notNullValue());
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getByIdOwner() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().minusDays(1L),
                itemDb,
                booker,
                BookingStatus.APPROVED);

        Booking booking1 = new Booking(null,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.APPROVED);

        em.persist(booking);
        em.persist(booking1);
        em.flush();
        ItemDto itemDto = itemService.getById(
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                userQuery.setParameter("name", owner.getName()).getSingleResult().getId());

        assertThat(itemDto, notNullValue());
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getLastBooking().getItem().getId(), equalTo(itemDb.getId()));
        assertThat(itemDto.getNextBooking().getItem().getId(), equalTo(itemDb.getId()));
    }

    @Test
    void getAllUserItems() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        user.setId(userQuery.setParameter("name", user.getName()).getSingleResult().getId());
        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                user,
                null);
        Item item1 = new Item(null,
                "УШМ",
                "nice",
                true,
                user,
                null);
        em.persist(item);
        em.persist(item1);
        em.flush();

        List<ItemDto> items = new ArrayList<>();
        items.add(itemMapper.toDto(item));
        items.add(itemMapper.toDto(item1));


        List<ItemDto> targetItems = itemService.getAllUserItems(
                userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                0,
                20);

        assertThat(targetItems, hasSize(items.size()));
        for (ItemDto sourceItem : items) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    @Test
    void searchItemsByNameOrDescription() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

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

        List<ItemDto> items = new ArrayList<>();
        items.add(itemMapper.toDto(item));
        items.add(itemMapper.toDto(item1));

        List<ItemDto> targetItems = itemService.searchItemsByNameOrDescription("RuB", 0, 20);

        assertThat(targetItems, hasSize(items.size()));
        for (ItemDto sourceItem : items) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    @Test
    void searchItemsByNameOrDescriptionShouldReturnEmptyList() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

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

        List<ItemDto> targetItems = itemService.searchItemsByNameOrDescription("", 0, 20);

        assertThat(targetItems, hasSize(0));
    }

    @Test
    void update() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        em.persist(user);
        em.flush();
        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        user.setId(userQuery.setParameter("name", user.getName()).getSingleResult().getId());
        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                user,
                null);
        em.persist(item);
        em.flush();

        ItemDto itemDto = new ItemDto(null,
                null,
                "notsocool",
                false,
                null,
                null,
                null,
                null);

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        ItemDto updatedItem = itemService.update(
                userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                itemDto);

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(updatedItem.getName(), equalTo(item.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void addComment() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        User booker = new User(null, "Eva", "eva@mail.ru");

        em.persist(user);
        em.persist(booker);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                null);
        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);
        TypedQuery<User> bookerQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(1L),
                itemQuery.setParameter("name", item.getName()).getSingleResult(),
                bookerQuery.setParameter("name", booker.getName()).getSingleResult(),
                BookingStatus.APPROVED);

        em.persist(booking);
        em.flush();

        CommentDto commentDto = new CommentDto(null,"good", "Eva", null);

        CommentDto comment = itemService.add(
                bookerQuery.setParameter("name", booker.getName()).getSingleResult().getId(),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                commentDto);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthorName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getCreated(), notNullValue());
    }

    @Test
    void badRequestException() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        User booker = new User(null, "Eva", "eva@mail.ru");

        em.persist(user);
        em.persist(booker);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null,
                "rubanok",
                "cool",
                true,
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                null);
        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);
        TypedQuery<User> bookerQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(1L),
                itemQuery.setParameter("name", item.getName()).getSingleResult(),
                bookerQuery.setParameter("name", booker.getName()).getSingleResult(),
                BookingStatus.APPROVED);

        em.persist(booking);
        em.flush();

        CommentDto commentDto = new CommentDto(null,"good", "Eva", null);


        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                itemService.add(bookerQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                        itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                        commentDto));

        assertEquals("Comment: Пользователь не может добавить комментарий к вещи," +
                " у которой он не был владельцем", exception.getMessage());
    }
}