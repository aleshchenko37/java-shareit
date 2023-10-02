package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.transfer.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull(groups = Exist.class) // Exist.class - применимо для обновляемых объектов Item
    private long id;
    @NotBlank(groups = {New.class, ExistUpdateDescription.class, ExistUpdateNameEmailDescAvail.class, ExistUpdateAvailable.class}) // New.class - применимо для новых объектов Item
    private String name;
    @NotBlank(groups = {New.class, ExistUpdateName.class, ExistUpdateNameEmailDescAvail.class, ExistUpdateAvailable.class})
    private String description;
    @AssertTrue(groups = {New.class, ExistUpdateNameEmailDescAvail.class})
    private boolean available;
    private long owner;
    private long request;

    public boolean getAvailable() {
        return available;
    }
}
