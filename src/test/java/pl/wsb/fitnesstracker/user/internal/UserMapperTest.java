package pl.wsb.fitnesstracker.user.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        
        testUser = createUserWithId(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        testUserDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
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
    void shouldMapUserToDto() {
        UserDto result = userMapper.toDto(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.id());
        assertEquals(testUser.getFirstName(), result.firstName());
        assertEquals(testUser.getLastName(), result.lastName());
        assertEquals(testUser.getBirthdate(), result.birthdate());
        assertEquals(testUser.getEmail(), result.email());
    }

    @Test
    void shouldMapUserToDtoWithNullId() {
        User userWithoutId = new User("Jane", "Smith", LocalDate.of(1995, 5, 15), "jane@example.com");
        
        UserDto result = userMapper.toDto(userWithoutId);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
        assertEquals(LocalDate.of(1995, 5, 15), result.birthdate());
        assertEquals("jane@example.com", result.email());
    }

    @Test
    void shouldMapDtoToEntity() {
        User result = userMapper.toEntity(testUserDto);

        assertNotNull(result);
        assertNull(result.getId()); // ID should not be set when creating new entity
        assertEquals(testUserDto.firstName(), result.getFirstName());
        assertEquals(testUserDto.lastName(), result.getLastName());
        assertEquals(testUserDto.birthdate(), result.getBirthdate());
        assertEquals(testUserDto.email(), result.getEmail());
    }

    @Test
    void shouldMapDtoToEntityWithNullId() {
        UserDto dtoWithoutId = new UserDto(null, "Alice", "Brown", LocalDate.of(2000, 3, 20), "alice@example.com");
        
        User result = userMapper.toEntity(dtoWithoutId);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Brown", result.getLastName());
        assertEquals(LocalDate.of(2000, 3, 20), result.getBirthdate());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void shouldMapUserToSimpleModel() {
        UserSimpleModel result = userMapper.toSimpleUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.id());
        assertEquals(testUser.getFirstName(), result.firstName());
        assertEquals(testUser.getLastName(), result.lastName());
    }

    @Test
    void shouldMapUserToSimpleModelWithNullId() {
        User userWithoutId = new User("Bob", "Green", LocalDate.of(1985, 7, 10), "bob@example.com");
        
        UserSimpleModel result = userMapper.toSimpleUser(userWithoutId);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Bob", result.firstName());
        assertEquals("Green", result.lastName());
    }

    @Test
    void shouldMapUserToIdAndEmail() {
        UserIdAndEmailRes result = userMapper.toIdAndEmail(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.id());
        assertEquals(testUser.getEmail(), result.email());
    }

    @Test
    void shouldMapUserToIdAndEmailWithNullId() {
        User userWithoutId = new User("Charlie", "White", LocalDate.of(1992, 11, 25), "charlie@example.com");
        
        UserIdAndEmailRes result = userMapper.toIdAndEmail(userWithoutId);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("charlie@example.com", result.email());
    }

    @Test
    void shouldHandleAllFieldsCorrectly() {
        // Test with all possible field values
        User userWithAllFields = createUserWithId(999L, "FirstName", "LastName", LocalDate.of(1980, 12, 31), "test@email.com");

        // Test toDto
        UserDto dto = userMapper.toDto(userWithAllFields);
        assertEquals(999L, dto.id());
        assertEquals("FirstName", dto.firstName());
        assertEquals("LastName", dto.lastName());
        assertEquals(LocalDate.of(1980, 12, 31), dto.birthdate());
        assertEquals("test@email.com", dto.email());

        // Test toEntity
        User entity = userMapper.toEntity(dto);
        assertNull(entity.getId()); // ID not set in toEntity
        assertEquals("FirstName", entity.getFirstName());
        assertEquals("LastName", entity.getLastName());
        assertEquals(LocalDate.of(1980, 12, 31), entity.getBirthdate());
        assertEquals("test@email.com", entity.getEmail());

        // Test toSimpleUser
        UserSimpleModel simple = userMapper.toSimpleUser(userWithAllFields);
        assertEquals(999L, simple.id());
        assertEquals("FirstName", simple.firstName());
        assertEquals("LastName", simple.lastName());

        // Test toIdAndEmail
        UserIdAndEmailRes idAndEmail = userMapper.toIdAndEmail(userWithAllFields);
        assertEquals(999L, idAndEmail.id());
        assertEquals("test@email.com", idAndEmail.email());
    }
}