package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ItemServiceImpl implements ItemService {
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    public ItemDto createItem(ItemDto dto, long userId) {
        userExistingCheck(userId);
        dto.setUserId(userId);
        Item item = ItemMapper.toItem(dto, userRepository.findById(userId).get());
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        userExistingCheck(userId);
        itemExistingCheck(itemId);
        dto.setUserId(userId);
        Item item = itemRepository.findById(itemId).get();
        accessCheck(item.getUser().getId(), userId);
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setIsAvailable(dto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public void deleteItem(long itemId, long userId) {
        userExistingCheck(userId);
        itemExistingCheck(itemId);
        Item item = itemRepository.findById(itemId).get();
        accessCheck(item.getUser().getId(), userId);
        itemRepository.deleteById(itemId);
    }

    public ItemDtoFull getItemById(long itemId, Long userId) {
        itemExistingCheck(itemId);
        Item item = itemRepository.findById(itemId).get();
        if (userId == item.getUser().getId()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Booking lastBooking = bookingRepository.getFirstByItemIdAndStatusNotAndEndBeforeOrderByEnd(itemId, Status.REJECTED, localDateTime);
            Booking nextBooking = bookingRepository.getTopByItemIdAndStatusNotAndStartAfterOrderByStart(itemId, Status.REJECTED, localDateTime);
            return ItemMapper.toItemDtoFull(item, lastBooking, nextBooking);
        } else return ItemMapper.toItemDtoFull(item, null, null);
    }

    public Collection<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDtoFull> getAllUsersItems(Long userId) {
        if (userId == null) {
            return itemRepository.findAll()
                    .stream()
                    .map(item -> ItemMapper.toItemDtoFull(item, null, null))
                    .collect(Collectors.toList());
        }
        userExistingCheck(userId);
        LocalDateTime localDateTime = LocalDateTime.now();
        return itemRepository.findByUserId(userId)
                .stream()
                .map(item -> ItemMapper.toItemDtoFull(item,
                        bookingRepository.getFirstByItemIdAndEndBeforeOrderByEnd(item.getId(), localDateTime),
                        bookingRepository.getTopByItemIdAndStartAfterOrderByStart(item.getId(), localDateTime)))
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> findItem(String text, Long userId) {
        if (text.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void userExistingCheck(long id) {
        if (userRepository.existsById(id) == false) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private void itemExistingCheck(long id) {
        if (itemRepository.existsById(id) == false) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
    }

    private void accessCheck(long userIdFromItem, long userIdFromRequest) {
        if (userIdFromItem != userIdFromRequest) {
            throw new WrongAccessException("Недостаточно прав для редактирования");
        }
    }
}
