package pl.wsb.fitnesstracker.user.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.wsb.fitnesstracker.exception.api.GlobalExceptionHandler;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProvider userProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDto testUserDto;
    private UserSimpleModel testUserSimple;
    private UserIdAndEmailRes testUserIdEmail;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUser = createUserWithId(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        testUserDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        testUserSimple = new UserSimpleModel(1L, "John", "Doe");
        testUserIdEmail = new UserIdAndEmailRes(1L, "john@example.com");
    }
    
    private User createUserWithId(Long id, String firstName, String lastName, LocalDate birthdate, String email) {
        User user = new User(firstName, lastName, birthdate, email);
        // Use reflection to set the ID since there's no setter
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
        return user;
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userProvider.findAllUsers()).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(userProvider).findAllUsers();
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldReturnSimpleUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userProvider.findAllUsers()).thenReturn(users);
        when(userMapper.toSimpleUser(any(User.class))).thenReturn(testUserSimple);

        mockMvc.perform(get("/v1/users/simple"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(userProvider).findAllUsers();
        verify(userMapper).toSimpleUser(any(User.class));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userProvider).getUser(1L);
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() throws Exception {
        when(userProvider.getUser(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("User with ID 999 not found", 
                        result.getResolvedException().getMessage()));

        verify(userProvider).getUser(999L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto newUserDto = new UserDto(null, "Jane", "Smith", LocalDate.of(1995, 5, 5), "jane@example.com");
        User newUser = new User("Jane", "Smith", LocalDate.of(1995, 5, 5), "jane@example.com");
        User savedUser = createUserWithId(2L, "Jane", "Smith", LocalDate.of(1995, 5, 5), "jane@example.com");
        UserDto savedUserDto = new UserDto(2L, "Jane", "Smith", LocalDate.of(1995, 5, 5), "jane@example.com");

        when(userMapper.toEntity(any(UserDto.class))).thenReturn(newUser);
        when(userService.createUser(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(savedUserDto);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Jane"));

        verify(userMapper).toEntity(any(UserDto.class));
        verify(userService).createUser(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void shouldReturnUsersByEmail() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userProvider.getUsersByPartOfEmail("john")).thenReturn(users);
        when(userMapper.toIdAndEmail(any(User.class))).thenReturn(testUserIdEmail);

        mockMvc.perform(get("/v1/users/email")
                        .param("email", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userProvider).getUsersByPartOfEmail("john");
        verify(userMapper).toIdAndEmail(any(User.class));
    }

    @Test
    void shouldReturnUsersOlderThan() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userProvider.getUsersOlderThan(30)).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        mockMvc.perform(get("/v1/users/older/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userProvider).getUsersOlderThan(30);
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto updateDto = new UserDto(1L, "John Updated", "Doe Updated", LocalDate.of(1990, 1, 1), "john.updated@example.com");
        
        doNothing().when(userService).updateUser(anyLong(), anyString(), anyString(), anyString(), any(LocalDate.class));

        mockMvc.perform(put("/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        verify(userService).updateUser(1L, "John Updated", "Doe Updated", "john.updated@example.com", LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldReturnBadRequestForInvalidUserData() throws Exception {
        UserDto invalidUserDto = new UserDto(null, "", "", null, "invalid-email");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }
}