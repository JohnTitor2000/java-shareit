package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapperTest {

    @Test
    public void testCommentDtoToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        Long userId = 1L;
        Long itemId = 2L;
        User author = new User();
        author.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        CommentMapper commentMapper = new CommentMapper();

        Comment comment = commentMapper.commentDtoToComment(commentDto, userId, itemId, author, item);

        Assertions.assertEquals(commentDto.getText(), comment.getText());
        Assertions.assertEquals(author, comment.getAuthor());
        Assertions.assertEquals(item, comment.getItem());
        Assertions.assertNotNull(comment.getCreated());
    }

    @Test
    public void testCommentToCommentDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        User author = new User();
        author.setId(1L);
        author.setName("John");

        comment.setAuthor(author);

        CommentMapper commentMapper = new CommentMapper();

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        Assertions.assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
