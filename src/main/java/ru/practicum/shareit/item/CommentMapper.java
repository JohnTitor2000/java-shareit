package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    public CommentMapper(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Comment commentDtoToComment(CommentDto commentDto, Long userId, Long itemId) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user not found!")));
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found!")));
        return comment;
    }

    public static CommentDto commentToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
