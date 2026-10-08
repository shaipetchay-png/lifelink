package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/donors")
public class AdminDonorController {

    private final UserRepository userRepository;

    public AdminDonorController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User currentAdmin(HttpSession session) {
        Object value = session.getAttribute("adminLoggedInUser");

        if (!(value instanceof User user)) {
            return null;
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return null;
        }

        return userRepository.findById(user.getId()).orElse(null);
    }

@GetMapping
public String donorList(
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        HttpSession session,
        Model model) {

    User admin = currentAdmin(session);

    if (admin == null) {
        return "redirect:/admin-login";
    }

    keyword = keyword == null ? "" : keyword.trim();

    List<User> donors;

    if (keyword.isEmpty()) {
        donors = userRepository.findByRoleIgnoreCaseOrderByIdDesc("DONOR");
    } else {
        donors = userRepository.searchDonors(keyword);
    }

    model.addAttribute("loggedInUser", admin);
    model.addAttribute("donors", donors);
    model.addAttribute("keyword", keyword);

    return "admin-donors";
}

    @GetMapping("/add")
    public String showAddDonor(HttpSession session, Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        model.addAttribute("loggedInUser", currentAdmin(session));
        model.addAttribute("donor", new User());
        return "admin-add-donor";
    }

    @PostMapping("/add")
    public String addDonor(
            @ModelAttribute("donor") User donor,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        donor.setFirstName(safeTrim(donor.getFirstName()));
        donor.setMiddleName(safeTrim(donor.getMiddleName()));
        donor.setLastName(safeTrim(donor.getLastName()));
        donor.setEmail(safeTrim(donor.getEmail()));
        donor.setContact(safeTrim(donor.getContact()));
        donor.setAddress(safeTrim(donor.getAddress()));
        donor.setBloodType(safeTrim(donor.getBloodType()));
        donor.setUsername(safeTrim(donor.getUsername()));

        if (donor.getFirstName() == null || donor.getFirstName().isBlank()
                || donor.getLastName() == null || donor.getLastName().isBlank()
                || donor.getEmail() == null || donor.getEmail().isBlank()
                || donor.getUsername() == null || donor.getUsername().isBlank()
                || donor.getPassword() == null || donor.getPassword().isBlank()) {
            model.addAttribute("loggedInUser", currentAdmin(session));
            model.addAttribute("error", "Please complete all required fields.");
            return "admin-add-donor";
        }

        if (userRepository.existsByUsername(donor.getUsername())) {
            model.addAttribute("loggedInUser", currentAdmin(session));
            model.addAttribute("error", "Username is already registered.");
            return "admin-add-donor";
        }

        if (userRepository.existsByEmail(donor.getEmail())) {
            model.addAttribute("loggedInUser", currentAdmin(session));
            model.addAttribute("error", "Email is already registered.");
            return "admin-add-donor";
        }

        if (!donor.getPassword().equals(confirmPassword)) {
            model.addAttribute("loggedInUser", currentAdmin(session));
            model.addAttribute("error", "Passwords do not match.");
            return "admin-add-donor";
        }

        if (donor.getBirthdate() != null) {
            donor.setAge(calculateAge(donor.getBirthdate()));
        }

        donor.setRole("DONOR");
        userRepository.save(donor);

        return "redirect:/admin/donors?added=true";
    }

    @GetMapping("/view/{id}")
    public String viewDonor(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        Optional<User> donor = userRepository.findById(id);

        if (donor.isEmpty() || !"DONOR".equalsIgnoreCase(donor.get().getRole())) {
            return "redirect:/admin/donors";
        }

        model.addAttribute("loggedInUser", currentAdmin(session));
        model.addAttribute("donor", donor.get());
        return "admin-donor-information";
    }

    @GetMapping("/edit/{id}")
    public String showEditDonor(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        Optional<User> donor = userRepository.findById(id);

        if (donor.isEmpty() || !"DONOR".equalsIgnoreCase(donor.get().getRole())) {
            return "redirect:/admin/donors";
        }

        model.addAttribute("loggedInUser", currentAdmin(session));
        model.addAttribute("donor", donor.get());
        return "admin-edit-donor";
    }

    @PostMapping("/edit/{id}")
    public String editDonor(
            @PathVariable Long id,
            @ModelAttribute("donor") User formDonor,
            HttpSession session,
            Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        Optional<User> existingOptional = userRepository.findById(id);

        if (existingOptional.isEmpty() ||
                !"DONOR".equalsIgnoreCase(existingOptional.get().getRole())) {
            return "redirect:/admin/donors";
        }

        User donor = existingOptional.get();

        formDonor.setFirstName(safeTrim(formDonor.getFirstName()));
        formDonor.setMiddleName(safeTrim(formDonor.getMiddleName()));
        formDonor.setLastName(safeTrim(formDonor.getLastName()));
        formDonor.setEmail(safeTrim(formDonor.getEmail()));
        formDonor.setContact(safeTrim(formDonor.getContact()));
        formDonor.setAddress(safeTrim(formDonor.getAddress()));
        formDonor.setBloodType(safeTrim(formDonor.getBloodType()));
        formDonor.setUsername(safeTrim(formDonor.getUsername()));

        if (formDonor.getFirstName() == null || formDonor.getFirstName().isBlank()
                || formDonor.getLastName() == null || formDonor.getLastName().isBlank()
                || formDonor.getEmail() == null || formDonor.getEmail().isBlank()
                || formDonor.getUsername() == null || formDonor.getUsername().isBlank()) {
            model.addAttribute("error", "First name, last name, email, and username are required.");
            model.addAttribute("donor", donor);
            return "admin-edit-donor";
        }

        Optional<User> usernameOwner = userRepository.findByUsername(formDonor.getUsername());

        if (usernameOwner.isPresent() && !usernameOwner.get().getId().equals(id)) {
            model.addAttribute("error", "Username is already used by another account.");
            model.addAttribute("donor", donor);
            return "admin-edit-donor";
        }

        if (userRepository.existsByEmail(formDonor.getEmail()) && !formDonor.getEmail().equalsIgnoreCase(donor.getEmail())) {
            model.addAttribute("error", "Email is already used by another account.");
            model.addAttribute("donor", donor);
            return "admin-edit-donor";
        }

        donor.setFirstName(formDonor.getFirstName());
        donor.setMiddleName(formDonor.getMiddleName());
        donor.setLastName(formDonor.getLastName());
        donor.setBirthdate(formDonor.getBirthdate());
        donor.setGender(formDonor.getGender());
        donor.setEmail(formDonor.getEmail());
        donor.setContact(formDonor.getContact());
        donor.setAddress(formDonor.getAddress());
        donor.setBloodType(formDonor.getBloodType());
        donor.setUsername(formDonor.getUsername());

        if (donor.getBirthdate() != null) {
            donor.setAge(calculateAge(donor.getBirthdate()));
        }

        userRepository.save(donor);

        return "redirect:/admin/donors?updated=true";
    }

    @PostMapping("/delete/{id}")
    public String deleteDonor(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (currentAdmin(session) == null) {
            return "redirect:/admin-login";
        }

        Optional<User> donor = userRepository.findById(id);

        if (donor.isEmpty() || !"DONOR".equalsIgnoreCase(donor.get().getRole())) {
            return "redirect:/admin/donors";
        }

        try {
            userRepository.delete(donor.get());
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            return "redirect:/admin/donors?deleteError=true";
        }

        return "redirect:/admin/donors?deleted=true";
    }

    private Integer calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
