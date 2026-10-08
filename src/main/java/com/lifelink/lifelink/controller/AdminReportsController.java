package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.Appointment;
import com.lifelink.lifelink.model.BloodInventory;
import com.lifelink.lifelink.model.BloodRequest;
import com.lifelink.lifelink.model.Donation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportsController {

    private final UserRepository users;
    private final DonationRepository donations;
    private final AppointmentRepository appointments;
    private final BloodRequestRepository requests;
    private final BloodInventoryRepository inventory;
    private final EligibilityAssessmentRepository assessments;

    public AdminReportsController(UserRepository users, DonationRepository donations,
                                  AppointmentRepository appointments, BloodRequestRepository requests,
                                  BloodInventoryRepository inventory, EligibilityAssessmentRepository assessments) {
        this.users = users;
        this.donations = donations;
        this.appointments = appointments;
        this.requests = requests;
        this.inventory = inventory;
        this.assessments = assessments;
    }

    private User currentAdmin(HttpSession session) {
        Object value = session.getAttribute("adminLoggedInUser");
        if (!(value instanceof User user) || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return null;
        }
        return users.findById(user.getId()).orElse(null);
    }

    @GetMapping
    public String reports(
            @RequestParam(defaultValue = "donors") String report,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String bloodType,
            @RequestParam(defaultValue = "") String dateFrom,
            @RequestParam(defaultValue = "") String dateTo,
            HttpSession session,
            Model model) {

        User admin = currentAdmin(session);
        if (admin == null) {
            return "redirect:/admin-login";
        }

        LocalDate from = parseDate(dateFrom);
        LocalDate to = parseDate(dateTo);
        String keyword = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String type = bloodType == null ? "" : bloodType.trim();
        String selectedReport = normalizeReport(report);

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("report", selectedReport);
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("bloodType", type);
        model.addAttribute("dateFrom", dateFrom == null ? "" : dateFrom);
        model.addAttribute("dateTo", dateTo == null ? "" : dateTo);

        switch (selectedReport) {
            case "donations" -> {
                List<Donation> rows = new ArrayList<>();
                for (Donation donation : donations.findAll()) {
                    if (matchesDonation(donation, keyword, type, from, to)) {
                        rows.add(donation);
                    }
                }
                model.addAttribute("rows", rows);
            }
            case "eligibility" -> {
                List<EligibilityAssessment> rows = new ArrayList<>();
                for (EligibilityAssessment assessment : assessments.findAllByOrderByAssessedAtDesc()) {
                    if (matchesEligibility(assessment, keyword, type, from, to)) {
                        rows.add(assessment);
                    }
                }
                model.addAttribute("rows", rows);
            }
            case "inventory" -> {
                List<BloodInventory> rows = new ArrayList<>();
                for (BloodInventory item : inventory.findAll()) {
                    if (type.isBlank() || type.equalsIgnoreCase(item.getBloodType())) {
                        rows.add(item);
                    }
                }
                model.addAttribute("rows", rows);
            }
            case "appointments" -> {
                List<Appointment> rows = new ArrayList<>();
                for (Appointment appointment : appointments.findAll()) {
                    if (matchesAppointment(appointment, keyword, type, from, to)) {
                        rows.add(appointment);
                    }
                }
                model.addAttribute("rows", rows);
            }
            default -> {
                List<User> rows = new ArrayList<>();
                for (User donor : users.findByRoleIgnoreCaseOrderByIdDesc("DONOR")) {
                    if (matchesDonor(donor, keyword, type)) {
                        rows.add(donor);
                    }
                }
                model.addAttribute("rows", rows);
            }
        }

        return "reports";
    }

    private String normalizeReport(String report) {
        if (report == null) return "donors";
        return switch (report.toLowerCase(Locale.ROOT)) {
            case "donation", "donations" -> "donations";
            case "eligibility" -> "eligibility";
            case "inventory", "blood-inventory" -> "inventory";
            case "appointment", "appointments" -> "appointments";
            default -> "donors";
        };
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean inDateRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) return false;
        if (from != null && date.isBefore(from)) return false;
        if (to != null && date.isAfter(to)) return false;
        return true;
    }

    private boolean contains(String value, String keyword) {
        return keyword.isBlank() || (value != null && value.toLowerCase(Locale.ROOT).contains(keyword));
    }

    private String donorName(User user) {
        if (user == null) return "";
        String first = user.getFirstName() == null ? "" : user.getFirstName();
        String last = user.getLastName() == null ? "" : user.getLastName();
        return (first + " " + last).trim();
    }

    private boolean matchesDonor(User user, String keyword, String bloodType) {
        boolean text = keyword.isBlank()
                || contains(user.getFirstName(), keyword)
                || contains(user.getMiddleName(), keyword)
                || contains(user.getLastName(), keyword)
                || contains(user.getUsername(), keyword)
                || contains(user.getEmail(), keyword)
                || contains(user.getContact(), keyword);
        boolean blood = bloodType.isBlank()
                || bloodType.equalsIgnoreCase(user.getBloodType());
        return text && blood;
    }

    private boolean matchesDonation(Donation donation, String keyword, String bloodType,
                                    LocalDate from, LocalDate to) {
        User donor = donation.getDonor();
        return contains(donorName(donor), keyword)
                && (bloodType.isBlank() || bloodType.equalsIgnoreCase(donation.getBloodType()))
                && inDateRange(donation.getDonationDate(), from, to);
    }

    private boolean matchesEligibility(EligibilityAssessment assessment, String keyword, String bloodType,
                                       LocalDate from, LocalDate to) {
        User donor = assessment.getDonor();
        LocalDate assessedDate = assessment.getAssessedAt() == null
                ? null : assessment.getAssessedAt().toLocalDate();
        String donorBloodType = donor == null ? "" : donor.getBloodType();
        return contains(donorName(donor), keyword)
                && (bloodType.isBlank() || bloodType.equalsIgnoreCase(donorBloodType))
                && inDateRange(assessedDate, from, to);
    }

    private boolean matchesAppointment(Appointment appointment, String keyword, String bloodType,
                                      LocalDate from, LocalDate to) {
        User donor = appointment.getDonor();
        String donorBloodType = donor == null ? "" : donor.getBloodType();
        return contains(donorName(donor), keyword)
                && (bloodType.isBlank() || bloodType.equalsIgnoreCase(donorBloodType))
                && inDateRange(appointment.getAppointmentDate(), from, to);
    }
}
