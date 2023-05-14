package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final HashMap<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long counter = 1;

    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        String email = user.getEmail();
        if (emails.contains(email)) {
            throw new ConflictException("Такой email уже используется");
        }
        user.setId(counter++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User #" + id + " not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto dto, long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("User #" + userId + " not found");
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            String email = dto.getEmail();
            if (!email.equalsIgnoreCase(user.getEmail()) && emails.contains(email)) {
                throw new ConflictException("Такой email уже используется");
            }
            emails.remove(user.getEmail());
            user.setEmail(dto.getEmail());
        }
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(long id) {
        UserDto user = getUser(id);
        if (user.getEmail() != null) {
            emails.remove(user.getEmail());
        }
        return UserMapper.toUserDto(users.remove(id));
    }
}
