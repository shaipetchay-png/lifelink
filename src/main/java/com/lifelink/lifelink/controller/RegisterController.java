package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class RegisterController {

    private final UserRepository userRepository;

    public RegisterController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(

            @RequestParam("firstName")
            String firstName,

            @RequestParam(value = "middleName", required = false)
            String middleName,

            @RequestParam("lastName")
            String lastName,

            @RequestParam("birthdate")
            String birthdate,

            @RequestParam("age")
            Integer age,

            @RequestParam("gender")
            String gender,

            @RequestParam("bloodType")
            String bloodType,

            @RequestParam("contact")
            String contact,

            @RequestParam("email")
            String email,

            @RequestParam("address")
            String address,

            @RequestParam("username")
            String username,

            @RequestParam("password")
            String password,

            @RequestParam("confirmPassword")
            String confirmPassword
    ) {

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password";
        }

        // Check duplicate username
        if (userRepository.existsByUsername(username)) {
            return "redirect:/register?error=username";
        }

        // Check duplicate email
        if (userRepository.existsByEmail(email)) {
            return "redirect:/register?error=email";
        }

        // Create new User
        User user = new User();

        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        user.setLastName(lastName);

        if (birthdate != null && !birthdate.isEmpty()) {
            user.setBirthdate(LocalDate.parse(birthdate));
        }

        user.setAge(age);
        user.setGender(gender);
        user.setBloodType(bloodType);
        user.setContact(contact);
        user.setEmail(email);
        user.setAddress(address);

        user.setUsername(username);
        user.setPassword(password);

        // Every newly registered account is a DONOR
        user.setRole("DONOR");

        // Save to MySQL
        userRepository.save(user);

        // Return to login page after successful registration
        return "redirect:/login?registered=true";
    }
}