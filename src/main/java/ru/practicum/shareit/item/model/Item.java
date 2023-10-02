package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
