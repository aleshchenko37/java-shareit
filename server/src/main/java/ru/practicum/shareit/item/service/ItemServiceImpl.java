package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public ItemDto createItem(ItemDto dto, long userId) {
        userExistingCheck(userId);
        dto.setUserId(userId);
        Item item;
        if (dto.getRequestId() != null) {
            item = ItemMapper.toItem(dto, userRepository.findById(userId).get(), itemRequestRepository.findById(dto.getRequestId()).get());
        } else {
            item = ItemMapper.toItem(dto, userRepository.findById(userId).get(), null);
        }
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        userExistingCheck(userId);
        itemExistingCheck(itemId);
        dto.setUserId(userId);
        Item item = itemRepository.findById(itemId).get();
        accessCheck(item.getUser().getId(), userId);
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setIsAvailable(dto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public void deleteItem(long itemId, long userId) {
        userExistingCheck(userId);
        itemExistingCheck(itemId);
        Item item = itemRepository.findById(itemId).get();
        accessCheck(item.getUser().getId(), userId);
        itemRepository.deleteById(itemId);
    }

    public ItemDtoFull getItemById(long itemId, Long userId) {
        itemExistingCheck(itemId);
        Item item = itemRepository.findById(itemId).get();
        List<CommentDtoFull> comments = getComments(itemId);
        if (userId != null && userId.equals(item.getUser().getId())) {
            userExistingCheck(userId);
            Booking lastBooking = bookingRepository.getFirstByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(itemId, Status.REJECTED, LocalDateTime.now());
            Booking nextBooking = bookingRepository.getFirstByItemIdAndStatusNotAndStartAfterOrderByStart(itemId, Status.REJECTED, LocalDateTime.now());
            return ItemMapper.toItemDtoFull(item, lastBooking, nextBooking, comments);
        } else return ItemMapper.toItemDtoFull(item, null, null, comments);
    }

    public Collection<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDtoFull> getAllUsersItems(Long userId) {
        if (userId == null) {
            return itemRepository.findAll()
                    .stream()
                    .map(item -> ItemMapper.toItemDtoFull(item, null, null, getComments(item.getId())))
                    .collect(Collectors.toList());
        }
        userExistingCheck(userId);
        LocalDateTime localDateTime = LocalDateTime.now();
        return itemRepository.findByUserId(userId)
                .stream()
                .map(item -> ItemMapper.toItemDtoFull(item,
                        bookingRepository.getFirstByItemIdAndEndBeforeOrderByEnd(item.getId(), localDateTime),
                        bookingRepository.getTopByItemIdAndStartAfterOrderByStart(item.getId(), localDateTime),
                        getComments(item.getId())))
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> findItem(String text, Long userId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDtoFull addComment(CommentDto commentDto, long itemId, long userId) {
        userExistingCheck(userId);
        itemExistingCheck(itemId);
        Booking booking = bookingRepository.findFirstByBookerId(userId);
        if (booking != null && booking.getEnd().isBefore(LocalDateTime.now())) {
            commentDto.setAuthor(userRepository.findById(userId).get().getId());
            commentDto.setCreated(LocalDateTime.now());
            Comment comment = CommentMapper.toComment(commentDto, itemRepository.findById(itemId).get(), userRepository.findById(userId).get());
            comment = commentRepository.save(comment);
            return CommentMapper.toCommentDtoFull(comment);
        } else {
            throw new WrongAccessException("Пользователь не брал предмет в аренду или срок аренды еще не истек");
        }

    }

    private void userExistingCheck(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private void itemExistingCheck(long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
    }

    private void accessCheck(long userIdFromItem, long userIdFromRequest) {
        if (userIdFromItem != userIdFromRequest) {
            throw new WrongAccessException("Недостаточно прав для редактирования");
        }
    }

    private List<CommentDtoFull> getComments(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDtoFull)
                .collect(Collectors.toList());
    }
}
