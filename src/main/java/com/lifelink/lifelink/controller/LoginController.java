package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // =========================
    // OPEN LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    // =========================
    // PROCESS LOGIN
    // =========================

    @PostMapping("/login")
    public String login(

            @RequestParam("username")
            String username,

            @RequestParam("password")
            String password,

            HttpSession session

    ) {

        // Find account using username
        User user = userRepository
                .findByUsername(username)
                .orElse(null);


        // Account does not exist
        if (user == null) {
            return "redirect:/login?error=invalid";
        }


        // Check password
        if (!user.getPassword().equals(password)) {
            return "redirect:/login?error=invalid";
        }


        System.out.println("USERNAME: " + user.getUsername());
        System.out.println("ROLE: " + user.getRole());


        // Check role
        if ("DONOR".equalsIgnoreCase(user.getRole())) {

            // Save the logged-in donor in the session
            session.setAttribute("donorLoggedInUser", user);

            return "redirect:/dashboard";
        }


        // If role is not supported
        return "redirect:/login?error=role";
    }
}