package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_ExistingId_ReturnsUser() {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John");
        expectedUser.setEmail("john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserById(userId);

        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NonExistingId_ThrowsNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateUser_ValidUser_ReturnsCreatedUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUser_NullEmail_ThrowsBadRequestException() {
        User user = new User();
        user.setName("John");
        user.setEmail(null);

        assertThrows(BadRequestException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ExistingUser_ReturnsUpdatedUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("johndoe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser, userId);

        assertEquals(updatedUser, result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_NonExistingUser_ThrowsNotFoundException() {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John");
        updatedUser.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(updatedUser, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_ExistingId_DeletesUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetAll_ReturnsAllUsers() {
        List<User> expectedUsers = new ArrayList<>();

        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");
        expectedUsers.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        expectedUsers.add(user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAll();

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }
}
