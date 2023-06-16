package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    private User user1;
    private User user2;
    private Item item;
    private Comment comment;
    private ItemRequest request;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user1 = userRepository.save(new User(-1, "name1", "mail1@yandex.com"));
        user2 = userRepository.save(new User(-1, "name2", "mail2@yandex.com"));
        request = requestRepository.save(new ItemRequest(-1, "description", user2.getId(), now));
        item = itemRepository.save(new Item(-1, "itemName", "description", true, user1, request.getId()));
        comment = commentRepository.save(new Comment(-1L, "comment", item, user2, now));
    }

    @Test
    void findByRequestorId() {
        List<ItemRequest> res = requestRepository.findByRequestorId(user2.getId(), Sort.by("created"));
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(request, res.get(0));
    }

    @Test
    void findByRequestorIdNot() {
        Page<ItemRequest> res = requestRepository.findByRequestorIdNot(user1.getId(), Pageable.unpaged());
        assertNotNull(res);
        assertTrue(!res.isEmpty());
        assertEquals(request, res.toList().get(0));
    }
}