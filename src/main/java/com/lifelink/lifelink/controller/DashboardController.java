package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.Appointment;
import com.lifelink.lifelink.model.Donation;
import com.lifelink.lifelink.model.EligibilityAssessment;
import com.lifelink.lifelink.model.User;
import com.lifelink.lifelink.repository.AppointmentRepository;
import com.lifelink.lifelink.repository.DonationRepository;
import com.lifelink.lifelink.repository.EligibilityAssessmentRepository;
import com.lifelink.lifelink.repository.BloodRequestRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DonationRepository donationRepository;
    private final EligibilityAssessmentRepository assessmentRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public DashboardController(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            DonationRepository donationRepository,
            EligibilityAssessmentRepository assessmentRepository,
            BloodRequestRepository bloodRequestRepository) {

        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.donationRepository = donationRepository;
        this.assessmentRepository = assessmentRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Object loggedInUser = session.getAttribute("donorLoggedInUser");

        // Walang naka-login
        if (!(loggedInUser instanceof User user)) {
            return "redirect:/login";
        }

        // Kunin ang latest/current user mula sa database
        User donor = userRepository
                .findById(user.getId())
                .orElse(null);

        if (donor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // =========================
        // DONOR INFORMATION
        // =========================

        model.addAttribute("loggedInUser", donor);
        model.addAttribute("bloodType", donor.getBloodType() == null ? "Not Set" : donor.getBloodType());


        // =========================
        // DONATION HISTORY
        // =========================

        List<Donation> donations =
                donationRepository.findByDonorOrderByDonationDateDesc(donor);

        model.addAttribute("donations", donations);

        model.addAttribute(
                "totalDonations",
                donations.size()
        );
        java.time.LocalDate nextEligibleDate = null;
        Donation latestDonation = donations.stream()
                .filter(x -> "COMPLETED".equalsIgnoreCase(x.getStatus()) && x.getDonationDate() != null)
                .findFirst().orElse(null);
        if (latestDonation != null) {
            // Prototype/system rule only; not a medical determination.
            nextEligibleDate = latestDonation.getDonationDate().plusMonths(6);
        }
        model.addAttribute("nextEligibleDate", nextEligibleDate);


        // =========================
        // UPCOMING APPOINTMENT
        // =========================

        List<Appointment> appointments =
                appointmentRepository
                        .findByDonorAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(
                                donor, java.time.LocalDate.now(), List.of("PENDING", "APPROVED"));

        Appointment upcomingAppointment = appointments.isEmpty() ? null : appointments.get(0);

        model.addAttribute(
                "upcomingAppointment",
                upcomingAppointment
        );


        // =========================
        // ELIGIBILITY
        // =========================

        EligibilityAssessment assessment =
                assessmentRepository
                        .findFirstByDonorOrderByAssessedAtDesc(donor)
                        .orElse(null);

        model.addAttribute(
                "assessment",
                assessment
        );

        model.addAttribute(
                "eligibilityStatus",
                assessment == null ? "IN_PROCESS" : assessment.getResult()
        );

        model.addAttribute(
                "bloodReadinessScore",
                assessment == null || assessment.getBloodReadinessScore() == null
                        ? 0 : assessment.getBloodReadinessScore()
        );


        model.addAttribute("recentRequests",
                donor.getBloodType() == null ? bloodRequestRepository.findTop10ByOrderByCreatedAtDesc()
                        : bloodRequestRepository.findTop10ByBloodTypeOrderByCreatedAtDesc(donor.getBloodType()));

        // =========================
        // RETURN DASHBOARD
        // =========================

        return "donor-dashboard";
    }
}