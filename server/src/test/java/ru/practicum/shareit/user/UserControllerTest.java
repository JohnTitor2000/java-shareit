package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_ExistingId_ReturnsUser() throws Exception {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John");
        expectedUser.setEmail("john@example.com");
        when(userService.getUserById(userId)).thenReturn(expectedUser);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserById_NonExistingId_ReturnsNotFound() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetAllUsers_ReturnsAllUsers() throws Exception {
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

        when(userService.getAll()).thenReturn(expectedUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService, times(1)).getAll();
    }

    @Test
    void testCreateUsers_AllValid() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        when(userService.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).createUser(user);
    }

    @Test
    void testCreateUsers_FailedDuplicateEmail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userService.createUser(user)).thenThrow(new BadRequestException("User Not Found"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\"}"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(user);
    }

    @Test
    void testCreateUsers_FailedNoEmail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");

        when(userService.createUser(user)).thenThrow(new BadRequestException("User should have email"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"John\"}"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(user);
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("john.doe@example.com");
        when(userService.updateUser(updatedUser, userId)).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testUpdateUser_InvalidId() throws Exception {
        Long userId = -1L;
        User updatedUser = new User();
        updatedUser.setId(-1L);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("john.doe@example.com");
        when(userService.updateUser(updatedUser, userId)).thenThrow(new BadRequestException(""));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": -1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateUser_InvalidRequestBody() throws Exception {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(userId);
        when(userService.updateUser(updatedUser, userId)).thenThrow(new BadRequestException(""));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_UserNotFound() throws Exception {
        Long userId = 1L;
        when(userService.updateUser(any(User.class), eq(userId))).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        Long userId = 1L;
        doThrow(new NotFoundException("User not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
