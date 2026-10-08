package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.BloodInventory;
import com.lifelink.lifelink.model.User;
import com.lifelink.lifelink.repository.BloodInventoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminInventoryController {

    private final UserRepository userRepository;
    private final BloodInventoryRepository inventoryRepository;

    public AdminInventoryController(UserRepository userRepository,
                                    BloodInventoryRepository inventoryRepository) {
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    private User currentAdmin(HttpSession session) {
        Object value = session.getAttribute("adminLoggedInUser");
        if (!(value instanceof User user) || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return null;
        }
        return userRepository.findById(user.getId()).orElse(null);
    }

    @GetMapping("/admin/inventory")
    public String inventory(HttpSession session, Model model) {
        User admin = currentAdmin(session);
        if (admin == null) {
            return "redirect:/admin-login";
        }

        List<BloodInventory> inventory = inventoryRepository.findAll();

        int totalUnits = inventory.stream()
                .mapToInt(item -> item.getUnits() == null ? 0 : item.getUnits())
                .sum();
        long availableTypes = inventory.stream()
                .filter(item -> item.getUnits() != null && item.getUnits() > 0)
                .count();
        long lowStockTypes = inventory.stream()
                .filter(item -> item.getUnits() != null && item.getUnits() > 0 && item.getUnits() <= 5)
                .count();

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("inventory", inventory);
        model.addAttribute("totalUnits", totalUnits);
        model.addAttribute("availableTypes", availableTypes);
        model.addAttribute("lowStockTypes", lowStockTypes);

        return "admin-inventory";
    }
}
