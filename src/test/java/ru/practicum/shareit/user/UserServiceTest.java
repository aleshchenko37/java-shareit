package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private UserService userService;
    private User user;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void prepare() {
        userService = new UserServiceImpl(userRepository);
        user = new User();
        user.setEmail("test@mail.ru");
        user.setName("test");
    }

    @Test
    public void addNewUser() {
        UserDto dto = new UserDto();
        dto.setName("test");
        dto.setEmail("test@mail.ru");
        UserDto expectedUserDto = new UserDto(1L, "test", "test@mail.ru");
        User user = new User(1L, "test", "test@mail.ru");

        when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        UserDto actualUserDto = userService.createUser(dto);
        assertEquals(expectedUserDto.getId(), actualUserDto.getId());
        assertEquals(expectedUserDto.getEmail(), actualUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void updateUser() {
        UserDto userDto = new UserDto(1L, "test", "test@mail.ru");
        User updateUser = new User(1L, "update_test", "update_test@mail.ru");
        user.setId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(updateUser);

        UserDto dto = userService.updateUser(userDto, 1L);
        assertEquals(dto.getName(), updateUser.getName());
        assertEquals(dto.getEmail(), updateUser.getEmail());
        assertEquals(dto.getId(), updateUser.getId());
    }

    @Test
    public void getUser_shouldReturnUser() {
        user.setId(1L);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        UserDto foundUser = userService.getUserById(user.getId());

        assertEquals(foundUser.getId(), user.getId());
        assertEquals(foundUser.getName(), user.getName());
        assertEquals(foundUser.getEmail(), user.getEmail());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(user.getId());
    }

    @Test
    public void getNotExistUser_shouldThrowException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    public void getUsers_shouldReturnUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        Collection<UserDto> users = userService.getAllUsers();
        List<UserDto> usersInList = new ArrayList<>(users);

        assertEquals(1, usersInList.size());
        assertEquals(user.getId(), usersInList.get(0).getId());
        assertEquals(user.getName(), usersInList.get(0).getName());
        assertEquals(user.getEmail(), usersInList.get(0).getEmail());
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }
}
