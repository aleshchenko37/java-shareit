package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto dto, long userId);

    ItemDto updateItem(ItemDto dto, long itemId,  long userId);

    ItemDto getItemById(long id, Long userId);

    Collection<ItemDto> getAllUsersItems(Long userId);

    Collection<ItemDto> findItem(String text, Long userId);
}
