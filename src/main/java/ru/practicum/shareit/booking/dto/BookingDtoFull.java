package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Formatter.PATTERN_FOR_BOOKING;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoFull {
    private long id;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime start;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime end;
    @NotNull
    private ItemDtoForBooking item;
    private UserDtoForBooking booker;
    private Status status;
}
