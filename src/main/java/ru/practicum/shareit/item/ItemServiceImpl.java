package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemServiceImpl implements ItemService {
    UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public ItemDto createItem(ItemDto dto, long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        dto.setOwner(userId); // пользователь, добавивший предмет, становится его владельцем
        dto.setId(nextId);
        nextId++;
        Item item = ItemMapper.toItem(dto);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        ItemDto itemUpdate = getItemById(itemId, userId); // проверка наличия пользователя с заданным id и проверка наличия предмета в базе
        if (itemUpdate.getOwner() != 0) {
            accessCheck(itemUpdate.getOwner(), userId);
        }
        if (dto.getName() != null) {
            itemUpdate.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            itemUpdate.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            itemUpdate.setAvailable(dto.getAvailable());
        }
        if (dto.getAvailable() != null) {
            itemUpdate.setAvailable(dto.getAvailable());
        }
        items.put(itemId, ItemMapper.toItem(itemUpdate));
        return itemUpdate;
    }

    public ItemDto getItemById(long id, long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        if (items.containsKey(id)) {
            return ItemMapper.toItemDto(items.get(id));
        } else {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
    }

    public Collection<ItemDto> getAllUsersItems(long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        return items.values()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> findItem(String text, long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        if (text.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        String textInLowerCase = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(textInLowerCase) || item.getDescription().toLowerCase().contains(textInLowerCase))
                .filter(item -> item.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(long id, long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        ItemDto dto = getItemById(id, userId); // проверка наличия предмета в базе
        accessCheck(dto.getOwner(), userId);
        items.remove(id);
    }

    public Collection<ItemDto> getAllItems(long userId) {
        userService.getUserById(userId); // проверка наличия пользователя с заданным id
        return items.values()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void accessCheck(long userIdFromItem, long userIdFromRequest) {
        if (userIdFromItem != userIdFromRequest) {
            throw new WrongAccessException("Недостаточно прав для редактирования");
        }
    }
}
