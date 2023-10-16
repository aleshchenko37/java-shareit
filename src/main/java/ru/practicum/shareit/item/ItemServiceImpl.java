package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ItemServiceImpl implements ItemService {
    UserRepository userRepository;
    ItemRepository itemRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public ItemDto createItem(ItemDto dto, long userId) {
        userExistingCheck(userId);
        dto.setUserId(userId);
        Item item = ItemMapper.toItem(dto, userRepository.findById(userId).get());
        itemRepository.save(item);
        return dto;
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

    public ItemDto getItemById(long itemId, Long userId) {
        itemExistingCheck(itemId);
        Item item = itemRepository.findById(itemId).get();
        return ItemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> getAllUsersItems(Long userId) {
        if (userId == null) {
            return itemRepository.findAll()
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        userExistingCheck(userId);
        return itemRepository.findByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
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
