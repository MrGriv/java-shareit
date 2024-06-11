package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private BookingService bookingService;
    private final EntityManager em;

    @Test
    void add() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null, "черенок", "отличный черенок", true,
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        BookingDtoIn bookingDtoIn = new BookingDtoIn(null,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                null, null);

        BookingDtoOut bookingDtoOut = bookingService.add(
                userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                bookingDtoIn);

        assertThat(bookingDtoOut.getId(), notNullValue());
        assertThat(bookingDtoOut.getStart(), equalTo(bookingDtoIn.getStart()));
        assertThat(bookingDtoOut.getEnd(), equalTo(bookingDtoIn.getEnd()));
        assertThat(bookingDtoOut.getItem(), notNullValue());
        assertThat(bookingDtoOut.getItem().getId(),
                equalTo(itemQuery.setParameter("name", item.getName()).getSingleResult().getId()));
        assertThat(bookingDtoOut.getBooker(), notNullValue());
        assertThat(bookingDtoOut.getBooker().getId(),
                equalTo(userQuery.setParameter("name", user.getName()).getSingleResult().getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void addThrowOwnerException() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null, "черенок", "отличный черенок", true,
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        BookingDtoIn bookingDtoIn = new BookingDtoIn(null,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                null, null);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.add(
                        userQuery.setParameter("name", owner.getName()).getSingleResult().getId(),
                        bookingDtoIn));

        assertEquals("Booking: Владелец не может бронировать свою вещь", exception.getMessage());
    }

    @Test
    void addThrowUnavailableException() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null, "черенок", "отличный черенок", false,
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        BookingDtoIn bookingDtoIn = new BookingDtoIn(null,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                null, null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.add(
                        userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                        bookingDtoIn));

        assertEquals("Item: Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void addThrowSameDateException() {
        User owner = new User(null, "Ivan", "iv@mail.ru");
        User user = new User(null, "Eva", "eva@mail.ru");

        em.persist(owner);
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("select u from User as u where u.name = :name", User.class);

        Item item = new Item(null, "черенок", "отличный черенок", true,
                userQuery.setParameter("name", owner.getName()).getSingleResult(),
                null);

        em.persist(item);
        em.flush();

        TypedQuery<Item> itemQuery = em.createQuery("select i from Item as i where i.name = :name", Item.class);

        BookingDtoIn bookingDtoIn = new BookingDtoIn(null,
                LocalDateTime.of(2200, 10, 20, 20, 20),
                LocalDateTime.of(2200, 10, 20, 20, 20),
                itemQuery.setParameter("name", item.getName()).getSingleResult().getId(),
                null, null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.add(
                        userQuery.setParameter("name", user.getName()).getSingleResult().getId(),
                        bookingDtoIn));

        assertEquals("Booking: Даты не могут совпадать", exception.getMessage());
    }

    @Test
    void changeBookingStatusApprove() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        TypedQuery<Booking> bookingQuery = em.createQuery("select b from Booking as b where b.item = :item", Booking.class);

        BookingDtoOut bookingDtoOut = bookingService.changeBookingStatus(ownerDb.getId(),
                bookingQuery.setParameter("item", itemDb).getSingleResult().getId(),
                true);

        assertThat(bookingDtoOut.getId(), notNullValue());
        assertThat(bookingDtoOut.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.getItem(), notNullValue());
        assertThat(bookingDtoOut.getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.getBooker(), notNullValue());
        assertThat(bookingDtoOut.getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void changeBookingStatusReject() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        TypedQuery<Booking> bookingQuery = em.createQuery("select b from Booking as b where b.item = :item",
                Booking.class);

        BookingDtoOut bookingDtoOut = bookingService.changeBookingStatus(ownerDb.getId(),
                bookingQuery.setParameter("item", itemDb).getSingleResult().getId(),
                false);

        assertThat(bookingDtoOut.getId(), notNullValue());
        assertThat(bookingDtoOut.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.getItem(), notNullValue());
        assertThat(bookingDtoOut.getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.getBooker(), notNullValue());
        assertThat(bookingDtoOut.getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void changeBookingStatusAgain() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.APPROVED);

        em.persist(booking);
        em.flush();

        TypedQuery<Booking> bookingQuery = em.createQuery("select b from Booking as b where b.item = :item",
                Booking.class);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.changeBookingStatus(ownerDb.getId(),
                        bookingQuery.setParameter("item", itemDb).getSingleResult().getId(),
                        false));

        assertEquals("Booking: Нельзя повторно менять статус", exception.getMessage());
    }

    @Test
    void getBooking() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        TypedQuery<Booking> bookingQuery = em.createQuery("select b from Booking as b where b.item = :item",
                Booking.class);

        BookingDtoOut bookingDtoOut = bookingService.getBooking(ownerDb.getId(),
                bookingQuery.setParameter("item", itemDb).getSingleResult().getId());

        assertThat(bookingDtoOut.getId(), notNullValue());
        assertThat(bookingDtoOut.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoOut.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoOut.getItem(), notNullValue());
        assertThat(bookingDtoOut.getItem().getId(),
                equalTo(itemDb.getId()));
        assertThat(bookingDtoOut.getBooker(), notNullValue());
        assertThat(bookingDtoOut.getBooker().getId(),
                equalTo(booker.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getAllUserBookings() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "ALL",
                0,
                20);

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
    void getFutureUserBookings() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "FUTURE",
                0,
                20);

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
    void getPastUserBookings() {
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
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "PAST",
                0,
                20);

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
    void getCurrentUserBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "CURRENT",
                0,
                20);

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
    void getWaitingUserBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "WAITING",
                0,
                20);

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
    void getRejectedUserBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.REJECTED);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllUserBookings(booker.getId(),
                "REJECTED",
                0,
                20);

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
    void getAllOwnerBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.REJECTED);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "ALL",
                0,
                20);

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
    void getFutureOwnerBookings() {
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
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "FUTURE",
                0,
                20);

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
    void getPastOwnerBookings() {
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
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "PAST",
                0,
                20);

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
    void getCurrentOwnerBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "CURRENT",
                0,
                20);

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
    void getWaitingOwnerBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.WAITING);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "WAITING",
                0,
                20);

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
    void getRejectedOwnerBookings() {
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
                LocalDateTime.now().plusDays(1L),
                itemDb,
                booker,
                BookingStatus.REJECTED);

        em.persist(booking);
        em.flush();

        List<BookingDtoOut> bookingDtoOut = bookingService.getAllOwnerBookings(ownerDb.getId(),
                "REJECTED",
                0,
                20);

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
}