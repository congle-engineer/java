package com.example.springauth.service;

import com.example.springauth.dto.UserRegistrationDto;
import com.example.springauth.model.User;
import com.example.springauth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_shouldSaveWithEncodedPassword() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        User saved = userService.registerUser(dto);

        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
        assertNotEquals("password123", saved.getPassword()); // BCrypt encoded
        assertTrue(saved.getPassword().startsWith("$2a$")); // BCrypt prefix
    }

    @Test
    void usernameExists_shouldReturnTrueForExistingUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("existinguser");
        dto.setEmail("existing@example.com");
        dto.setPassword("password123");
        userService.registerUser(dto);

        assertTrue(userService.usernameExists("existinguser"));
        assertFalse(userService.usernameExists("nonexistent"));
    }

    @Test
    void emailExists_shouldReturnTrueForExistingEmail() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("emailtestuser");
        dto.setEmail("emailtest@example.com");
        dto.setPassword("password123");
        userService.registerUser(dto);

        assertTrue(userService.emailExists("emailtest@example.com"));
        assertFalse(userService.emailExists("nope@example.com"));
    }
}
