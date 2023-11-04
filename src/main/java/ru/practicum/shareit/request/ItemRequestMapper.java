package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester().getId())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requester(user)
                .build();
    }

    public static ItemRequestDtoFull toItemRequestDtoFull(ItemRequest itemRequest, Set<Item> items) {
        Set<ItemDtoForRequest> requestsItemsSet = new HashSet<>();
        if (items != null) {
            requestsItemsSet = items
                    .stream()
                    .map(ItemMapper::toItemDtoForRequest)
                    .collect(Collectors.toSet());
        }

        return ItemRequestDtoFull.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester().getId())
                .items(requestsItemsSet)
                .build();
    }
}
