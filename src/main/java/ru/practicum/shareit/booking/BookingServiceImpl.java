package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;

public class BookingServiceImpl {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingDto createBooking(BookingDto dto, long itemId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        dto.setStart(Instant.now());
        dto.setItem(itemId);
        dto.setBooker(userId);
        dto.setStatus(Status.WAITING);
        Booking booking = BookingMapper.toBooking(dto, itemRepository.findById(itemId).get(), userRepository.findById(userId).get());
        bookingRepository.save(booking);
        return dto;
    }
}
