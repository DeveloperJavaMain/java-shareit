package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository repository;

    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        User user = repository.getReferenceById(id);
        if (user == null) {
            throw new NotFoundException("User #" + id + " not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> res = repository.findAll().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
        return res;
    }

    @Override
    public UserDto updateUser(UserDto dto, long userId) {
        User user = repository.getReferenceById(userId);
        if (user == null) {
            throw new NotFoundException("User #" + userId + " not found");
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            String email = dto.getEmail();
            user.setEmail(dto.getEmail());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(long id) {
        UserDto user = getUser(id);
        repository.deleteById(id);
        return user;
    }
}