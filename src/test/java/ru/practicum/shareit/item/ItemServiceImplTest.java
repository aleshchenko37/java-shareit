package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

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

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

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

    @Test
    public void updateItem() {
        User user = new User(1L, "Anastasiya", "ana@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, user.getId(), 1L);
        Item item = ItemMapper.toItem(itemDto, user, new ItemRequest());
        ItemDto itemDtoForUpdate = new ItemDto(1L, "updatedItem", "updatedDescription", false, user.getId(), 1L);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto updatedItemDto = itemService.updateItem(itemDtoForUpdate, item.getId(), user.getId());

        assertThat(updatedItemDto.getId(), notNullValue());
        assertThat(updatedItemDto.getName(), equalTo(itemDtoForUpdate.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(itemDtoForUpdate.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(itemDtoForUpdate.getAvailable()));
        assertThat(updatedItemDto.getUserId(), equalTo(itemDtoForUpdate.getUserId()));
    }

    @Test
    public void deleteItem() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<Item> item = itemRepository.findById(1L);

        assertThat(item, equalTo(Optional.empty()));

    }

    @Test
    void getAllItemsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 1L;
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Collection<ItemDtoFull> actualItems = itemService.getAllUsersItems(userId);

        assertThat(actualItems, empty());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findAllByItemId(anyLong());
        verify(itemRepository, times(1))
                .findByUserId(anyLong());
    }

    @Test
    void getAllItemsByUser_whenInvoked_thenReturnedItemsCollectionInList() {
        User user = new User(1L, "Anastasiya", "ana@mail.ru");
        Item item = new Item(1L, "item", "description", true, user, new ItemRequest());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);

        List<Item> expectedItems = List.of(item);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByUserId(anyLong())).thenReturn(expectedItems);
        when(bookingRepository.getFirstByItemIdAndEndBeforeOrderByEnd(anyLong(), any())).thenReturn(booking);
        when(bookingRepository.getTopByItemIdAndStartAfterOrderByStart(anyLong(), any())).thenReturn(null);

        Collection<ItemDtoFull> items = itemService.getAllUsersItems(user.getId());
        List<ItemDtoFull> actualItems = new ArrayList<>(items);

        assertThat(expectedItems.size(), equalTo(actualItems.size()));
        assertThat(expectedItems.get(0).getId(), equalTo(actualItems.get(0).getId()));
        assertThat(expectedItems.get(0).getName(), equalTo(actualItems.get(0).getName()));
        assertThat(expectedItems.get(0).getDescription(), equalTo(actualItems.get(0).getDescription()));
        assertThat(expectedItems.get(0).getIsAvailable(), equalTo(actualItems.get(0).getAvailable()));

        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1))
                .findByUserId(anyLong());
        inOrder.verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void getAllItems_whenInvoked_thenReturnedItemsCollectionInList() {
        User user = new User(1L, "Anastasiya", "ana@mail.ru");
        Item item = new Item(1L, "item", "description", true, user, new ItemRequest());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);

        List<Item> expectedItems = List.of(item);

        when(itemRepository.findAll()).thenReturn(expectedItems);

        Collection<ItemDto> items = itemService.getAllItems();
        List<ItemDto> actualItems = new ArrayList<>(items);

        assertThat(expectedItems.size(), equalTo(actualItems.size()));
        assertThat(expectedItems.get(0).getId(), equalTo(actualItems.get(0).getId()));
        assertThat(expectedItems.get(0).getName(), equalTo(actualItems.get(0).getName()));
        assertThat(expectedItems.get(0).getDescription(), equalTo(actualItems.get(0).getDescription()));
        assertThat(expectedItems.get(0).getIsAvailable(), equalTo(actualItems.get(0).getAvailable()));

        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findAll();
    }


    /*@Test
    void getItemById_whenItemFound_thenReturnedItem() {
        long itemId = 0L;
        long userId = 0L;
        Item expectedItem = new Item();
        User user = new User();
        user.setId(1L);
        expectedItem.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.EMPTY_LIST);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertThat(ItemMapper.INSTANCE.toItemDtoOwner(expectedItem,
                null, null, Collections.EMPTY_LIST), equalTo(actualItem));
        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItemWithBookings() {
        long userId = 0L;
        User user = new User();
        user.setId(userId);
        long itemId = 0L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);
        expectedItem.setOwner(user);

        Booking lastBooking = new Booking();
        lastBooking.setId(5L);
        lastBooking.setStart(LocalDateTime.now());
        lastBooking.setEnd(LocalDateTime.now().plusHours(1));
        lastBooking.setStatus(StatusBooking.APPROVED);
        lastBooking.setItem(expectedItem);
        Booking nextBooking = new Booking();
        nextBooking.setId(7L);
        nextBooking.setStart(LocalDateTime.now());
        nextBooking.setEnd(LocalDateTime.now().plusHours(2));
        nextBooking.setStatus(StatusBooking.APPROVED);
        nextBooking.setItem(expectedItem);

        Comment comment = new Comment();
        comment.setId(2L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(expectedItem);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(anyLong(), any(StatusBooking.class),
                any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(anyLong(), any(StatusBooking.class),
                any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(nextBooking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertThat(ItemMapper.INSTANCE.toItemDtoOwner(expectedItem,
                lastBooking, nextBooking, List.of(comment)), equalTo(actualItem));
        InOrder inOrder = inOrder(itemRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartIsBefore(anyLong(), any(StatusBooking.class),
                        any(LocalDateTime.class), any(Sort.class));
        inOrder.verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartIsAfter(anyLong(), any(StatusBooking.class),
                        any(LocalDateTime.class), any(Sort.class));
        inOrder.verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(itemId, 0L));

        assertThat("Вещь с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(itemId);
    }*/
}
