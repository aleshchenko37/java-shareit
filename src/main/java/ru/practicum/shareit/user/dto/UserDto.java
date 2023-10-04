package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.transfer.Exist;
import ru.practicum.shareit.transfer.ExistUpdateEmail;
import ru.practicum.shareit.transfer.ExistUpdateName;
import ru.practicum.shareit.transfer.New;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    @NotNull(groups = Exist.class) // Exist.class - применимо для обновляемых объектов User
    private long id;
    @NotBlank(groups = {New.class, ExistUpdateEmail.class}) // New.class - применимо для новых объектов User
    private String name;
    @NotBlank(groups = {New.class, ExistUpdateName.class})
    @Email(groups = {New.class, ExistUpdateName.class})
    private String email;
}
