package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;
    private final List<String> emails = new ArrayList<>();

    public UserDto createUser(UserDto dto) {
        checkEmail(dto.getEmail());
        dto.setId(nextId);
        nextId++;
        User user = UserMapper.toUser(dto);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return dto;
    }

    public UserDto updateUser(UserDto dto, long userId) {
        UserDto userUpdate = getUserById(userId); // тут же проверка наличия пользователя с заданным id
        if (dto.getName() == null) {
            checkEmail(dto.getEmail());
            userUpdate.setEmail(dto.getEmail());
        } else if (dto.getEmail() == null) {
            userUpdate.setName(dto.getName());
        } else {
            userUpdate.setName(dto.getName());
            userUpdate.setEmail(dto.getEmail());
        }
        users.put(userId, UserMapper.toUser(userUpdate));
        return userUpdate;
    }

    public void deleteUser(long id) {
        UserDto dto = getUserById(id); // тут же проверка наличия пользователя с заданным id
        emails.remove(dto.getEmail());
        users.remove(id);
    }

    public Collection<UserDto> getAllUsers() {
        return users.values()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        if (users.containsKey(id)) {
            return UserMapper.toUserDto(users.get(id));
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private void checkEmail(String email) {
        if (emails.contains(email)) {
            throw new ValidationException("Пользователь с email " + email + " уже зарегистрирован");
        }
    }
}
