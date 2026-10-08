package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AdminLoginController {

    private final UserRepository userRepository;

    public AdminLoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin-login";
    }

    @PostMapping("/admin-login")
    public String adminLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Invalid admin username or password.");
            return "admin-login";
        }

        User user = userOptional.get();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute(
                    "error",
                    "This account does not have administrator access."
            );
            return "admin-login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute(
                    "error",
                    "Invalid admin username or password."
            );
            return "admin-login";
        }

        session.setAttribute("adminLoggedInUser", user);

        return "redirect:/admin-dashboard";
    }
    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminLoggedInUser");
        return "redirect:/admin-login";
    }

}