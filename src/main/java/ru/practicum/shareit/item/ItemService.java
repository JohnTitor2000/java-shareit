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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ItemService {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserService userService;
    private ItemMapper itemMapper;
    private CommentRepository commentRepository;
    private CommentMapper commentMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService, ItemMapper itemMapper, BookingRepository bookingRepository, CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    public List<Item> getAllItems() {
        return null;
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Comment cant be empty.");
        }
        if (bookingRepository.findByBooker_IdOrderByStartDesc(userId).stream().filter(o -> o.getItem().getId().equals(itemId)).filter(o -> o.getStatus().equals(BookingStatus.APPROVED)).filter(o -> o.getEnd().isBefore(LocalDateTime.now())).count() == 0) {
            throw new BadRequestException("You cant add this comment");
        }
        Comment comment = commentRepository.save(commentMapper.commentDtoToComment(commentDto, userId, itemId));
        return CommentMapper.commentToCommentDto(comment);
    }

    public ItemDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        if (item.getOwner().getId().equals(userId)) {
            BookingIdAndBookerId next = bookingRepository.findBookingsNext(id, item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsNext(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getId(), bookingRepository.findBookingsNext(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            BookingIdAndBookerId last = bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getId(),bookingRepository.findBookingsLast(id, item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            return itemMapper.itemToItemDTO(item, last, next);
        } else {
            return itemMapper.itemToItemDTO(item, null, null);
        }
    }

    public List<ItemDto> getItemsByUser(Long userId) {
        if (userService.getUserById(userId).equals(null)) {
            throw new NotFoundException("User not found");
        }
        List<Item> items = itemRepository.findByOwnerIdOrderById(userId);
        List<ItemDto> itemsWithDates = new ArrayList<>();
        for (Item item: items) {
            BookingIdAndBookerId next = bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().get().getId(), bookingRepository.findBookingsNext(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            BookingIdAndBookerId last = bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().isPresent() ? new BookingIdAndBookerId(bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(),PageRequest.of(0, 1)).stream().findFirst().get().getId(),bookingRepository.findBookingsLast(item.getId(), item.getOwner().getId(), PageRequest.of(0, 1)).stream().findFirst().get().getBooker().getId()) : null;
            itemsWithDates.add(itemMapper.itemToItemDTO(item, last, next));
        }
        return itemsWithDates;
    }

    public Item createItem(Item item, Long userId) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getName() == null
                || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("The item status must be provided in the request.");
        }
        if (userId != null && userService.getUserById(userId) == null) {
            throw new NotFoundException("User not found.");
        }
        item.setOwner(userService.getUserById(userId));
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, ItemDto itemDTO, Long userId) {
        itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!userId.equals(itemRepository.findById(id).get().getOwner().getId())) {
            throw new NotFoundException("You can't change owner of item");
        }
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        return  itemRepository.save(item);
    }

    public void deleteItem(Long id) {
    }

    public List<Item> search(String text) {
        if (text.isBlank()) {
            List<Item> empty = Collections.EMPTY_LIST;
            return empty;
        }
        return itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text);
    }
}