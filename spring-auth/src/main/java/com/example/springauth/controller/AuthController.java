package com.example.springauth.controller;

import com.example.springauth.dto.UserRegistrationDto;
import com.example.springauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult result,
            Model model) {

        if (userService.usernameExists(dto.getUsername())) {
            result.rejectValue("username", "error.user", "Username already taken");
        }
        if (userService.emailExists(dto.getEmail())) {
            result.rejectValue("email", "error.user", "Email already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }

        userService.registerUser(dto);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
