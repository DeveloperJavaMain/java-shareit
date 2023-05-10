package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUser(long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto user, long userId);

    UserDto deleteUser(long id);
}
