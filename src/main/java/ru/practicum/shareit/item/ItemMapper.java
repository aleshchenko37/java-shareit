package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {
    public static Item toItem(ItemDto dto, User user) {
        return Item.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .isAvailable(dto.getAvailable())
            .user(user)
            .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getIsAvailable())
            .userId(item.getUser().getId())
            .build();
    }
}
