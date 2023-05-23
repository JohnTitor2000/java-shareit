package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserService userService;
    private ItemMapper itemMapper;
    private CommentRepository commentRepository;
    private CommentMapper commentMapper;
    private UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService, ItemMapper itemMapper, BookingRepository bookingRepository, CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
    }

    public List<ItemDtoDefault> getAllItems() {
        return itemRepository.findAll().stream().map(itemMapper::itemToItemDtoDefault).collect(Collectors.toList());
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Comment cant be empty.");
        }
        if (bookingRepository.findByBookerIdOrderByStartDesc(userId).stream().filter(o -> o.getItem().getId().equals(itemId)).filter(o -> o.getStatus().equals(BookingStatus.APPROVED)).filter(o -> o.getEnd().isBefore(LocalDateTime.now())).count() == 0) {
            throw new BadRequestException("You cant add this comment");
        }
        Comment comment = commentRepository.save(commentMapper.commentDtoToComment(commentDto, userId, itemId,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found.")),
                itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."))));
        return CommentMapper.commentToCommentDto(comment);
    }

    public ItemDtoWithBookings getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        if (item.getOwner().getId().equals(userId)) {
            BookingIdAndBookerId next = bookingRepository.findBookingsNext(id, item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsNext(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getId(), bookingRepository.findBookingsNext(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            BookingIdAndBookerId last = bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getId(),bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            return itemMapper.itemToItemDtoWithBookings(item, last, next, commentRepository.findByItemId(item.getId()));
        } else {
            return itemMapper.itemToItemDtoWithBookings(item, null, null, commentRepository.findByItemId(item.getId()));
        }
    }

    public List<ItemDtoWithBookings> getItemsByUser(Long userId) {
        if (userService.getUserById(userId).equals(null)) {
            throw new NotFoundException("User not found");
        }
        List<Item> items = itemRepository.findByOwnerIdOrderById(userId);
        List<ItemDtoWithBookings> itemsWithDates = new ArrayList<>();
        for (Item item: items) {
            BookingIdAndBookerId next = bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().get().getId(), bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            BookingIdAndBookerId last = bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().get().getId(),bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            itemsWithDates.add(itemMapper.itemToItemDtoWithBookings(item, last, next, commentRepository.findByItemId(item.getId())));
        }
        return itemsWithDates;
    }

    public ItemDtoDefault createItem(Item item, Long userId) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getName() == null
                || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("The item status must be provided in the request.");
        }
        if (userId != null && userService.getUserById(userId) == null) {
            throw new NotFoundException("User not found.");
        }
        item.setOwner(userService.getUserById(userId));
        return itemMapper.itemToItemDtoDefault(itemRepository.save(item));
    }

    public ItemDtoDefault updateItem(Long id, ItemDtoWithBookings itemDTOWithBookings, Long userId) {
        itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!userId.equals(itemRepository.findById(id).get().getOwner().getId())) {
            throw new NotFoundException("You can't change owner of item");
        }
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        if (itemDTOWithBookings.getName() != null) {
            item.setName(itemDTOWithBookings.getName());
        }
        if (itemDTOWithBookings.getDescription() != null) {
            item.setDescription(itemDTOWithBookings.getDescription());
        }
        if (itemDTOWithBookings.getAvailable() != null) {
            item.setAvailable(itemDTOWithBookings.getAvailable());
        }
        return itemMapper.itemToItemDtoDefault(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<ItemDtoDefault> search(String text) {
        if (text.isBlank()) {
            List empty = Collections.EMPTY_LIST;
            return empty;
        }
        return itemRepository.findByDescription(text).stream().map(itemMapper::itemToItemDtoDefault).collect(Collectors.toList());
    }
}