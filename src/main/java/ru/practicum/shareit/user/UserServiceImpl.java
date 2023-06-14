package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Override
    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        user = repository.save(user);
        log.info("User created {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User #" + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> res = repository.findAll().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
        log.info("Users found {}", res.size());
        return res;
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto dto, long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new BadRequestException("Имя не должно быть пустым или состоять из пробелов");
            }
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            String email = dto.getEmail();
            if (dto.getEmail().isEmpty()) {
                throw new BadRequestException("Email не должно быть пустым");
            }
            user.setEmail(dto.getEmail());
        }
        user = repository.save(user);
        log.info("User updated {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto deleteUser(long id) {
        UserDto user = getUser(id);
        repository.deleteById(id);
        log.info("User deleted {}", user);
        return user;
    }
}