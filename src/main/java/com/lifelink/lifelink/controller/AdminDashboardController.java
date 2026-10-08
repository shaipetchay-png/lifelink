package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.Appointment;
import com.lifelink.lifelink.model.BloodInventory;
import com.lifelink.lifelink.model.BloodRequest;
import com.lifelink.lifelink.model.EligibilityAssessment;
import com.lifelink.lifelink.model.User;
import com.lifelink.lifelink.repository.AppointmentRepository;
import com.lifelink.lifelink.repository.BloodInventoryRepository;
import com.lifelink.lifelink.repository.BloodRequestRepository;
import com.lifelink.lifelink.repository.DonationRepository;
import com.lifelink.lifelink.repository.EligibilityAssessmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final EligibilityAssessmentRepository assessmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final BloodInventoryRepository inventoryRepository;
    private final DonationRepository donationRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            EligibilityAssessmentRepository assessmentRepository,
            AppointmentRepository appointmentRepository,
            BloodRequestRepository bloodRequestRepository,
            BloodInventoryRepository inventoryRepository,
            DonationRepository donationRepository) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.inventoryRepository = inventoryRepository;
        this.donationRepository = donationRepository;
    }

    private User currentAdmin(HttpSession session) {
        Object value = session.getAttribute("adminLoggedInUser");

        if (!(value instanceof User user) || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return null;
        }

        return userRepository.findById(user.getId()).orElse(null);
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User admin = currentAdmin(session);

        if (admin == null) {
            return "redirect:/admin-login";
        }

        LocalDate today = LocalDate.now();

        List<User> donors = userRepository.findByRoleIgnoreCaseOrderByIdDesc("DONOR");

        List<Appointment> upcomingAppointments = appointmentRepository
                .findByAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(
                        today,
                        List.of("PENDING", "APPROVED"));

        List<BloodRequest> recentRequests = bloodRequestRepository
                .findTop10ByOrderByCreatedAtDesc()
                .stream()
                .limit(5)
                .toList();

        List<EligibilityAssessment> recentAssessments = assessmentRepository
                .findAllByOrderByAssessedAtDesc()
                .stream()
                .limit(5)
                .toList();

        List<BloodInventory> inventory = inventoryRepository.findAll();

        long totalDonors = donors.size();
        long totalDonations = donationRepository.countByStatusIgnoreCase("COMPLETED");
        long upcomingAppointmentCount = upcomingAppointments.size();
        long bloodRequestCount = bloodRequestRepository.count();
        long totalInventoryUnits = inventory.stream()
                .mapToLong(item -> item.getUnits() == null ? 0L : item.getUnits())
                .sum();

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("totalDonors", totalDonors);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("upcomingAppointmentCount", upcomingAppointmentCount);
        model.addAttribute("bloodRequestCount", bloodRequestCount);
        model.addAttribute("totalInventoryUnits", totalInventoryUnits);
        model.addAttribute("recentDonors", donors.stream().limit(5).toList());
        model.addAttribute("upcomingAppointments", upcomingAppointments.stream().limit(5).toList());
        model.addAttribute("recentRequests", recentRequests);
        model.addAttribute("recentAssessments", recentAssessments);
        model.addAttribute("inventory", inventory);

        return "admin-dashboard";
    }
}
