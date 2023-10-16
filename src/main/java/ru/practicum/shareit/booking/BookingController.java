package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.transfer.New;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{itemId}")
    public BookingDto createBooking(@RequestBody @Validated(New.class) BookingDto dto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(dto, itemId, userId);
    }
}
