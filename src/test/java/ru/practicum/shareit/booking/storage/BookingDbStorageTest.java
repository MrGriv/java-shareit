package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingDbStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingDbStorage bookingStorage;

    @Test
    void findAllOwnerBookings() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.getEntityManager()
                .createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.REJECTED);

        em.persist(booking);
        em.flush();

        List<Booking> bookingDtoOut = bookingStorage.findAllOwnerBookings(ownerDb.getId(),
                PageRequest.of(0, 20)).getContent();

        assertThat(bookingDtoOut.get(0).getId(), notNullValue());
        assertThat(bookingDtoOut.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.get(0).getItem(), notNullValue());
        assertThat(bookingDtoOut.get(0).getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.get(0).getBooker(), notNullValue());
        assertThat(bookingDtoOut.get(0).getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.get(0).getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void findAllOwnerFutureBookings() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.getEntityManager()
                .createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<Booking> bookingDtoOut = bookingStorage.findAllOwnerFutureBookings(ownerDb.getId(), LocalDateTime.now(),
                PageRequest.of(0, 20)).getContent();

        assertThat(bookingDtoOut.get(0).getId(), notNullValue());
        assertThat(bookingDtoOut.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.get(0).getItem(), notNullValue());
        assertThat(bookingDtoOut.get(0).getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.get(0).getBooker(), notNullValue());
        assertThat(bookingDtoOut.get(0).getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void findAllOwnerPastBookings() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.getEntityManager()
                .createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().minusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<Booking> bookingDtoOut = bookingStorage.findAllOwnerPastBookings(ownerDb.getId(), LocalDateTime.now(),
                PageRequest.of(0, 20)).getContent();

        assertThat(bookingDtoOut.get(0).getId(), notNullValue());
        assertThat(bookingDtoOut.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.get(0).getItem(), notNullValue());
        assertThat(bookingDtoOut.get(0).getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.get(0).getBooker(), notNullValue());
        assertThat(bookingDtoOut.get(0).getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void findAllOwnerCurrentBookings() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.getEntityManager()
                .createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<Booking> bookingDtoOut = bookingStorage.findAllOwnerCurrentBookings(ownerDb.getId(), LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(0, 20)).getContent();

        assertThat(bookingDtoOut.get(0).getId(), notNullValue());
        assertThat(bookingDtoOut.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.get(0).getItem(), notNullValue());
        assertThat(bookingDtoOut.get(0).getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.get(0).getBooker(), notNullValue());
        assertThat(bookingDtoOut.get(0).getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void findAllOwnerBookingsByStatus() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.getEntityManager()
                .createQuery("select u from User as u where u.name = :name", User.class);

        User ownerDb = userQuery.setParameter("name", owner.getName()).getSingleResult();

        Item item = new Item(null, "черенок", "отличный черенок", true,
                ownerDb,
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.getEntityManager()
                .createQuery("select i from Item as i where i.name = :name", Item.class);

        Item itemDb = itemQuery.setParameter("name", item.getName()).getSingleResult();
        User booker = userQuery.setParameter("name", user.getName()).getSingleResult();

        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<Booking> bookingDtoOut = bookingStorage.findAllOwnerBookingsByStatus(ownerDb.getId(),
                BookingStatus.WAITING,
                PageRequest.of(0, 20)).getContent();

        assertThat(bookingDtoOut.get(0).getId(), notNullValue());
        assertThat(bookingDtoOut.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.get(0).getItem(), notNullValue());
        assertThat(bookingDtoOut.get(0).getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.get(0).getBooker(), notNullValue());
        assertThat(bookingDtoOut.get(0).getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }
}