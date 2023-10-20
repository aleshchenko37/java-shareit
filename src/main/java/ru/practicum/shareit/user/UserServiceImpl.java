package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto createUser(UserDto dto) {
        //checkIfEmailIsUnique(dto.getEmail());
        User user = repository.save(UserMapper.toUser(dto));
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(UserDto dto, long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        user.setId(userId);
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    public void deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalStateException("User not found");
        }
    }

    public Collection<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
        return UserMapper.toUserDto(user);
    }

    /*public void checkIfEmailIsUnique(String email) {
        Optional<User> user = repository.findByEmail(email);
        if (user.isPresent()) {
            throw new ValidationException("Пользователь с таким email уже зарегистрирован");
        }
    }*/
}
