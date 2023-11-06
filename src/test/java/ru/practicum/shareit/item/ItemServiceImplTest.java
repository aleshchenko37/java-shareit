package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Test
    public void createItem() {
        User user = new User(1L, "Anastasiya", "ana@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, user.getId(), 1L);
        Item item = ItemMapper.toItem(itemDto, user, new ItemRequest());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new ItemRequest()));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto savedItemDto = itemService.createItem(itemDto, user.getId());

        assertThat(savedItemDto.getId(), notNullValue());
        assertThat(savedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(savedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(savedItemDto.getUserId(), equalTo(itemDto.getUserId()));
    }
}
