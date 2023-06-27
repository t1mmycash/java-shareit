package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserCannotCommentItemException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build()
        ));
    }

    @Override
    public CommentResponseDto addComment(long userId, long itemId, Comment comment) {
        User user = getUserById(userId);
        Item item = getItemById(itemId);
        if (bookingRepository.findFirstByItem_IdAndBooker_IdAndStatus(itemId, userId, BookingStatus.APPROVED).isEmpty()) {
            throw new UserCannotCommentItemException(
                    String.format("Пользователь с id = %d не может комментировать вещь с id = %d, " +
                            "т.к. не брал её в аренду", userId, itemId));
        }
        return CommentMapper.toCommentResponseDto(commentRepository.save(Comment.builder()
                .text(comment.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build()
        ));
    }

    @Override
    public List<ItemGetResponseDto> getAllUserItems(long userId) {
        userExistenceCheck(userId);
        List<ItemDto> items = itemRepository.findItemsByOwnerId(userId);
        List<ItemGetResponseDto> result = new ArrayList<>();
        for (ItemDto itemDto : items) {
            List<BookingInItemDto> itemBookings = bookingRepository.findAllItemBookingsByItemId(itemDto.getId(), BookingStatus.APPROVED);
            result.add(ItemMapper.toItemWithBookingsDto(
                    itemDto, findLastBookingOfItem(itemBookings), findNextBookingOfItem(itemBookings), findCommentsOfItem(itemDto.getId())));
        }
        return result;
    }

    @Override
    public ItemGetResponseDto getItemResponseDtoById(long userId, long itemId) {
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (userId == item.getOwner().getId()) {
            List<BookingInItemDto> itemBookings = bookingRepository.findAllItemBookingsByItemId(itemId, BookingStatus.APPROVED);
            return ItemMapper.toItemWithBookingsDto(
                    itemDto, findLastBookingOfItem(itemBookings), findNextBookingOfItem(itemBookings), findCommentsOfItem(itemId));
        } else {
            return ItemMapper.toItemWithBookingsDto(itemDto, null, null, findCommentsOfItem(itemId));
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemUpdateDto changes) {
        userExistenceCheck(userId);
        Item item = getItemById(itemId);
        if (userId != item.getOwner().getId()) {
            throw new AccessDeniedException(
                    String.format("У пользователя с id = %d нет доступа к вещи с id = %d", userId, itemId));
        }
        if (changes.getName().isPresent()) {
            item.setName(changes.getName().get());
        }
        if (changes.getDescription().isPresent()) {
            item.setDescription(changes.getDescription().get());
        }
        if (changes.getAvailable().isPresent()) {
            item.setAvailable(changes.getAvailable().get());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text.toLowerCase());
    }

    private BookingInItemDto findLastBookingOfItem(Collection<BookingInItemDto> itemBookings) {
        return itemBookings.stream().filter(b -> (b.getEnd().isBefore(LocalDateTime.now())
                        || (b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now()))))
                .max(Comparator.comparing(BookingInItemDto::getEnd))
                .orElse(null);
    }

    private BookingInItemDto findNextBookingOfItem(Collection<BookingInItemDto> itemBookings) {
        return itemBookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingInItemDto::getStart))
                .orElse(null);
    }

    private List<CommentResponseDto> findCommentsOfItem(long itemId) {
        return commentRepository.findAllByItem_Id(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    private void userExistenceCheck(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id = %d не существует", userId));
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findItemById(itemId, Item.class).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещи с id = %d не существует", itemId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("Пользователя с id = %d не существует", userId)));
    }

}
