package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.transfer.Exist;
import ru.practicum.shareit.transfer.New;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Validated(New.class) ItemDto dto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Validated(Exist.class) ItemDto dto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(dto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItem(text, userId);
    }
}
