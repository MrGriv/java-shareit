package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoOutTest {
    @Autowired
    private JacksonTester<BookingDtoOut> json;

    @Test
    void testBookingDtoOut() throws Exception {
        User owner = new User(1L, "Ivan", "iv@mail.ru");
        User booker = new User(2L, "Eva", "eva@mail.ru");
        Item item = new Item(1L, "черенок", "отличный черенок", true,
                owner,
                null);
        BookingDtoOut bookingDtoOut = new BookingDtoOut(1L,
                LocalDateTime.of(2200, 10, 15, 10, 20),
                LocalDateTime.of(2200, 10, 17, 10, 20),
                item,
                booker,
                BookingStatus.WAITING);

        JsonContent<BookingDtoOut> result = json.write(bookingDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2200-10-15T10:20:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2200-10-17T10:20:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}