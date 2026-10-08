package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.EligibilityAssessment;
import com.lifelink.lifelink.model.User;
import com.lifelink.lifelink.repository.EligibilityAssessmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/eligibility")
public class AdminEligibilityController {

    private final UserRepository userRepository;
    private final EligibilityAssessmentRepository assessmentRepository;

    public AdminEligibilityController(UserRepository userRepository,
                                      EligibilityAssessmentRepository assessmentRepository) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
    }

    private User currentAdmin(HttpSession session) {
        Object value = session.getAttribute("adminLoggedInUser");
        if (!(value instanceof User user)) return null;
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) return null;
        return userRepository.findById(user.getId()).orElse(null);
    }

    @GetMapping
    public String review(HttpSession session, Model model) {
        User admin = currentAdmin(session);
        if (admin == null) return "redirect:/admin-login";

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("assessments", assessmentRepository.findAllByOrderByAssessedAtDesc());
        return "admin-eligibility-review";
    }
}
