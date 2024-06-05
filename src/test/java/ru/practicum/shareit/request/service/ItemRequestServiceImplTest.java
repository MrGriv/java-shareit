package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
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

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ItemRequestService itemRequestService;
    private final EntityManager em;

    @Test
    void add() {
        User user = new User(null, "Ivan", "iv@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(null,
                "щетка для подошвы",
                null,
                null);

        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        itemRequestService.add(userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                itemRequestDto);

        TypedQuery<ItemRequest> itemRequestQuery = em.createQuery(
                "select ir from ItemRequest as ir where ir.description = :description",
                ItemRequest.class);

        ItemRequest itemRequest = itemRequestQuery.setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), notNullValue());
    }

    @Test
    void getUserRequestsExceptOwnerRequests() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        ItemRequest itemRequest = new ItemRequest(null,
                "щетка для подошвы",
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                LocalDateTime.now());

        ItemRequest itemRequest1 = new ItemRequest(null,
                "черенок",
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                LocalDateTime.now().minusDays(1L));

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.flush();

        TypedQuery<ItemRequest> itemRequestQuery = em.createQuery(
                "select ir from ItemRequest as ir where ir.description = :description",
                ItemRequest.class);

        Item item = new Item(null, "черенок", "отличный черенок", true,
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                itemRequestQuery.setParameter("description", itemRequest1.getDescription())
                .getSingleResult().getId());

        em.persist(item);
        em.flush();

        List<ItemRequestDto> targetItemRequests = itemRequestService.getAllRequests(userQuery
                .setParameter("name", owner.getName())
                .getSingleResult()
                .getId(),
                0,
                20);

        assertThat(targetItemRequests, hasSize(1));
        assertThat(targetItemRequests.get(0).getId(), notNullValue());
        assertThat(targetItemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequest.getCreated(), notNullValue());
        assertThat(targetItemRequests.get(0).getItems(), hasSize(1));
        assertThat(targetItemRequests.get(0).getItems().get(0).getId(), notNullValue());
        assertThat(targetItemRequests.get(0).getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(targetItemRequests.get(0).getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(targetItemRequests.get(0).getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(targetItemRequests.get(0).getItems().get(0).getRequestId(), equalTo(itemRequestQuery
                .setParameter("description", itemRequest1.getDescription())
                .getSingleResult().getId()));
    }

    @Test
    void getAllRequests() {
        User user = new User(null, "Ivan", "iv@mail.ru");

        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        ItemRequest itemRequest = new ItemRequest(null,
                "щетка для подошвы",
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                LocalDateTime.now());

        ItemRequest itemRequest1 = new ItemRequest(null,
                "черенок",
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                LocalDateTime.now().minusDays(1L));

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.flush();

        List<ItemRequestDto> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequestMapper.toDto(itemRequest));
        itemRequests.add(itemRequestMapper.toDto(itemRequest1));

        List<ItemRequestDto> targetItemRequests = itemRequestService.getUserRequests(userQuery
                .setParameter("name", user.getName())
                .getSingleResult()
                .getId());

        assertThat(targetItemRequests, hasSize(itemRequests.size()));
        for (ItemRequestDto sourceItemRequest : itemRequests) {
            assertThat(targetItemRequests, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                    hasProperty("created", equalTo(sourceItemRequest.getCreated())),
                    hasProperty("items", equalTo(new ArrayList<>()))
            )));
        }
    }

    @Test
    void getById() {
        User user = new User(null, "Ivan", "iv@mail.ru");

        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        ItemRequest itemRequest = new ItemRequest(null,
                "щетка для подошвы",
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                LocalDateTime.now());

        ItemRequest itemRequest1 = new ItemRequest(null,
                "черенок",
                userQuery.setParameter("name", user.getName()).getSingleResult(),
                LocalDateTime.now().minusDays(1L));

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.flush();

        TypedQuery<ItemRequest> itemRequestQuery = em.createQuery(
                "select ir from ItemRequest as ir where ir.description = :description",
                ItemRequest.class);

        ItemRequestDto itemRequestDto = itemRequestService.getById(userQuery
                .setParameter("name", user.getName())
                .getSingleResult()
                .getId(), itemRequestQuery.setParameter("description", itemRequest1.getDescription())
                .getSingleResult().getId());

        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequestDto.getCreated(), notNullValue());
    }
}