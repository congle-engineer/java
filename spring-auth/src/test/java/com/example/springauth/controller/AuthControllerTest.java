package com.example.springauth.controller;

import com.example.springauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getRegister_shouldReturnRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void getLogin_shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void postRegister_withValidData_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void postRegister_withBlankUsername_shouldReturnErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "")
                        .param("email", "new@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void postRegister_withDuplicateUsername_shouldReturnError() throws Exception {
        // Register first user
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "duplicate")
                .param("email", "first@example.com")
                .param("password", "password123"));

        // Try duplicate username
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "duplicate")
                        .param("email", "second@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}
