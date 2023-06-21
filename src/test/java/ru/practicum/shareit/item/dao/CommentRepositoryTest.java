package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Item item;
    private Comment comment;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(-1, "name1", "mail1@yandex.com"));
        user2 = userRepository.save(new User(-1, "name2", "mail2@yandex.com"));
        item = itemRepository.save(new Item(-1, "itemName", "description", true, user1, null));
        now = LocalDateTime.now();
        comment = commentRepository.save(new Comment(-1L, "comment", item, user2, now));
    }

    @Test
    void findByItemId() {
        List<Comment> res = commentRepository.findByItemId(item.getId());
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(comment, res.get(0));
    }

    @Test
    void findByItem_IdIn() {
        List<Comment> res = commentRepository.findByItem_IdIn(List.of(item.getId()), Sort.by("created"));
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(comment, res.get(0));
    }
}