package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto dto, Item item, User user) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(user)
                .status(dto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getId())
                .booker(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}
