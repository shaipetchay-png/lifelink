package com.lifelink.lifelink.controller;

import com.lifelink.lifelink.model.Appointment;
import com.lifelink.lifelink.model.BloodRequest;
import com.lifelink.lifelink.model.Donation;
import com.lifelink.lifelink.model.EligibilityAssessment;
import com.lifelink.lifelink.model.User;
import com.lifelink.lifelink.repository.AppointmentRepository;
import com.lifelink.lifelink.repository.BloodRequestRepository;
import com.lifelink.lifelink.repository.DonationRepository;
import com.lifelink.lifelink.repository.EligibilityAssessmentRepository;
import com.lifelink.lifelink.controller.model.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DonorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DonationRepository donationRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final EligibilityAssessmentRepository assessmentRepository;

    public DonorController(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            DonationRepository donationRepository,
            BloodRequestRepository bloodRequestRepository,
            EligibilityAssessmentRepository assessmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.donationRepository = donationRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.assessmentRepository = assessmentRepository;
    }

    private User currentDonor(HttpSession session) {
        Object value = session.getAttribute("donorLoggedInUser");
        if (!(value instanceof User user)) return null;
        return userRepository.findById(user.getId()).orElse(null);
    }

    private String requireDonor(HttpSession session) {
        return currentDonor(session) == null ? "redirect:/login" : null;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("donorLoggedInUser");
        return "redirect:/login";
    }

    @GetMapping("/donor-information")
    public String donorInformation(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        List<Donation> donations = donationRepository.findByDonorOrderByDonationDateDesc(donor);
        List<Donation> completedDonations = donations.stream()
                .filter(d -> "COMPLETED".equalsIgnoreCase(d.getStatus()) && d.getDonationDate() != null)
                .toList();
        Donation latestDonation = completedDonations.isEmpty() ? null : completedDonations.get(0);

        model.addAttribute("loggedInUser", donor);
        model.addAttribute("totalDonations", completedDonations.size());
        model.addAttribute("lastDonation", latestDonation == null ? null : latestDonation.getDonationDate());
        return "donor-information";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        model.addAttribute("loggedInUser", donor);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String contact,
            @RequestParam String address,
            HttpSession session) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";

        donor.setFirstName(firstName.trim());
        donor.setLastName(lastName.trim());
        donor.setEmail(email.trim());
        donor.setContact(contact.trim());
        donor.setAddress(address.trim());
        User saved = userRepository.save(donor);
        session.setAttribute("donorLoggedInUser", saved);
        return "redirect:/profile?saved=true";
    }

    @GetMapping("/eligibility")
    public String eligibility(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";

        EligibilityAssessment assessment = assessmentRepository
                .findFirstByDonorOrderByAssessedAtDesc(donor)
                .orElse(null);

        model.addAttribute("loggedInUser", donor);
        model.addAttribute("assessment", assessment);
        return "assessments";
    }

    @PostMapping("/eligibility/submit")
    public String submitEligibility(
            @RequestParam(required = false) Double weightKg,
            @RequestParam(required = false) Boolean feelingWell,
            @RequestParam(required = false) Boolean meetsAgeRequirement,
            @RequestParam(required = false) Boolean meetsWeightRequirement,
            @RequestParam(required = false) Boolean waitingPeriodComplete,
            @RequestParam(required = false) String previousDonation,
            @RequestParam(required = false) String lastDonationDate,
            @RequestParam(required = false) Boolean feverOrIllness,
            @RequestParam(required = false) Boolean takingMedication,
            @RequestParam(required = false) Boolean recentSurgery,
            @RequestParam(required = false) Boolean tattooOrPiercingRecently,
            @RequestParam(required = false) Boolean medicalCondition,
            @RequestParam(required = false) Boolean advisedNotToDonate,
            @RequestParam(required = false) Boolean recentVaccination,
            @RequestParam(required = false) Boolean recentDentalProcedure,
            @RequestParam(required = false) Boolean recentTravel,
            @RequestParam(required = false) Boolean transfusionOrTransplant,
            @RequestParam(required = false) String pregnancyStatus,
            HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        String validationError = validateAssessment(weightKg, feelingWell, meetsAgeRequirement, meetsWeightRequirement,
                waitingPeriodComplete, previousDonation, lastDonationDate, feverOrIllness, takingMedication,
                recentSurgery, tattooOrPiercingRecently, medicalCondition, advisedNotToDonate, recentVaccination,
                recentDentalProcedure, recentTravel, transfusionOrTransplant, pregnancyStatus);
        if (validationError != null) {
            model.addAttribute("loggedInUser", donor); model.addAttribute("assessment", null);
            model.addAttribute("error", validationError); return "assessments";
        }
        EligibilityAssessment a = new EligibilityAssessment();
        a.setDonor(donor); a.setWeightKg(weightKg); a.setFeelingWell(feelingWell);
        a.setMeetsAgeRequirement(meetsAgeRequirement); a.setMeetsWeightRequirement(meetsWeightRequirement);
        a.setWaitingPeriodComplete(waitingPeriodComplete); a.setPreviousDonation(previousDonation);
        if (lastDonationDate != null && !lastDonationDate.isBlank()) a.setLastDonationDate(LocalDate.parse(lastDonationDate));
        a.setFeverOrIllness(feverOrIllness); a.setTakingMedication(takingMedication); a.setRecentSurgery(recentSurgery);
        a.setTattooOrPiercingRecently(tattooOrPiercingRecently); a.setMedicalCondition(medicalCondition);
        a.setAdvisedNotToDonate(advisedNotToDonate); a.setRecentVaccination(recentVaccination);
        a.setRecentDentalProcedure(recentDentalProcedure); a.setRecentTravel(recentTravel);
        a.setTransfusionOrTransplant(transfusionOrTransplant); a.setPregnancyStatus(pregnancyStatus);
        a.setBloodReadinessScore(calculateBloodReadinessScore(feelingWell, meetsAgeRequirement, meetsWeightRequirement,
                waitingPeriodComplete, previousDonation, feverOrIllness, takingMedication, recentSurgery,
                tattooOrPiercingRecently, medicalCondition, advisedNotToDonate, recentVaccination,
                recentDentalProcedure, recentTravel, transfusionOrTransplant, pregnancyStatus));
        a.setResult(calculateEligibility(feelingWell, meetsAgeRequirement, meetsWeightRequirement, waitingPeriodComplete,
                previousDonation, feverOrIllness, takingMedication, recentSurgery, tattooOrPiercingRecently,
                medicalCondition, advisedNotToDonate, recentVaccination, recentDentalProcedure, recentTravel,
                transfusionOrTransplant, pregnancyStatus));
        a.setAssessedAt(LocalDateTime.now()); assessmentRepository.save(a);
        return "redirect:/eligibility?saved=true";
    }

    private String validateAssessment(Double weightKg, Boolean feelingWell, Boolean meetsAgeRequirement,
            Boolean meetsWeightRequirement, Boolean waitingPeriodComplete, String previousDonation,
            String lastDonationDate, Boolean feverOrIllness, Boolean takingMedication, Boolean recentSurgery,
            Boolean tattooOrPiercingRecently, Boolean medicalCondition, Boolean advisedNotToDonate,
            Boolean recentVaccination, Boolean recentDentalProcedure, Boolean recentTravel,
            Boolean transfusionOrTransplant, String pregnancyStatus) {
        if (weightKg == null) return "Please enter your weight.";
        if (weightKg < 30 || weightKg > 300) return "Please enter a valid weight between 30 and 300 kg.";
        if (feelingWell == null || meetsAgeRequirement == null || meetsWeightRequirement == null
                || waitingPeriodComplete == null || previousDonation == null || previousDonation.isBlank()
                || feverOrIllness == null || takingMedication == null || recentSurgery == null
                || tattooOrPiercingRecently == null || medicalCondition == null || advisedNotToDonate == null
                || recentVaccination == null || recentDentalProcedure == null || recentTravel == null
                || transfusionOrTransplant == null || pregnancyStatus == null || pregnancyStatus.isBlank())
            return "Please answer all health assessment questions.";
        if ("YES".equals(previousDonation) && (lastDonationDate == null || lastDonationDate.isBlank()))
            return "Please provide the date of your most recent blood donation.";
        if (lastDonationDate != null && !lastDonationDate.isBlank()) {
            try { if (LocalDate.parse(lastDonationDate).isAfter(LocalDate.now())) return "The most recent donation date cannot be in the future."; }
            catch (Exception e) { return "Please enter a valid most recent blood donation date."; }
        }
        return null;
    }

    // Educational/demo score for the supplied LifeLink questionnaire. The PDF does not specify a scoring formula.
    private int calculateBloodReadinessScore(Boolean feelingWell, Boolean age, Boolean weight, Boolean waiting,
            String previousDonation, Boolean fever, Boolean medication, Boolean surgery, Boolean tattoo,
            Boolean condition, Boolean advised, Boolean vaccination, Boolean dental, Boolean travel,
            Boolean transfusion, String pregnancy) {
        int score = 0;
        if (Boolean.TRUE.equals(feelingWell)) score += 10; if (Boolean.TRUE.equals(age)) score += 10;
        if (Boolean.TRUE.equals(weight)) score += 10;
        if (Boolean.TRUE.equals(waiting) || "NEVER".equals(previousDonation)) score += 10;
        if (Boolean.FALSE.equals(fever)) score += 10; if (Boolean.FALSE.equals(medication)) score += 5;
        if (Boolean.FALSE.equals(surgery)) score += 5; if (Boolean.FALSE.equals(tattoo)) score += 5;
        if (Boolean.FALSE.equals(condition)) score += 5; if (Boolean.FALSE.equals(advised)) score += 5;
        if (Boolean.FALSE.equals(vaccination)) score += 5; if (Boolean.FALSE.equals(dental)) score += 5;
        if (Boolean.FALSE.equals(travel)) score += 5; if (Boolean.FALSE.equals(transfusion)) score += 5;
        if ("NO".equals(pregnancy) || "NA".equals(pregnancy)) score += 5;
        return Math.min(score, 100);
    }

    private String calculateEligibility(Boolean feelingWell, Boolean age, Boolean weight, Boolean waiting,
            String previousDonation, Boolean fever, Boolean medication, Boolean surgery, Boolean tattoo,
            Boolean condition, Boolean advised, Boolean vaccination, Boolean dental, Boolean travel,
            Boolean transfusion, String pregnancy) {
        if (!Boolean.TRUE.equals(feelingWell) || !Boolean.TRUE.equals(age) || !Boolean.TRUE.equals(weight)
                || ("YES".equals(previousDonation) && !Boolean.TRUE.equals(waiting))) return "NOT_ELIGIBLE";
        if (Boolean.TRUE.equals(fever) || Boolean.TRUE.equals(medication) || Boolean.TRUE.equals(surgery)
                || Boolean.TRUE.equals(tattoo) || Boolean.TRUE.equals(condition) || Boolean.TRUE.equals(advised)
                || Boolean.TRUE.equals(vaccination) || Boolean.TRUE.equals(dental) || Boolean.TRUE.equals(travel)
                || Boolean.TRUE.equals(transfusion) || "YES".equals(pregnancy)) return "IN_PROCESS";
        return "ELIGIBLE";
    }

    @GetMapping("/schedule-donation")
    public String scheduleDonation(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        EligibilityAssessment assessment = assessmentRepository
                .findFirstByDonorOrderByAssessedAtDesc(donor).orElse(null);
        model.addAttribute("loggedInUser", donor);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("assessment", assessment);
        return "schedule-donation";
    }

    @PostMapping("/schedule-donation")
    public String createAppointment(
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String location,
            HttpSession session) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";

        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);
            if (appointmentDate.isBefore(LocalDate.now())) {
                return "redirect:/schedule-donation?error=past";
            }
            if (location == null || location.trim().isBlank()) {
                return "redirect:/schedule-donation?error=location";
            }

            List<Appointment> active = appointmentRepository
                    .findByDonorAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(
                            donor, LocalDate.now(), List.of("PENDING", "APPROVED"));
            if (!active.isEmpty()) {
                return "redirect:/schedule-donation?error=existing";
            }

            Appointment appointment = new Appointment();
            appointment.setDonor(donor);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setLocation(location.trim());
            appointment.setStatus("PENDING");
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            return "redirect:/schedule-donation?error=invalid";
        }
        return "redirect:/appointment?saved=true";
    }

    @GetMapping("/appointment")
    public String appointment(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        List<Appointment> appointments =
                appointmentRepository.findByDonorOrderByAppointmentDateDescAppointmentTimeDesc(donor);
        model.addAttribute("loggedInUser", donor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("today", LocalDate.now());
        return "appointment";
    }

    @PostMapping("/appointment/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        appointmentRepository.findById(id).filter(a -> a.getDonor().getId().equals(donor.getId())).ifPresent(a -> {
            a.setStatus("CANCELLED");
            appointmentRepository.save(a);
        });
        return "redirect:/appointment";
    }

    @PostMapping("/appointment/reschedule/{id}")
    public String rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestParam String time,
            HttpSession session) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        try {
            Appointment a = appointmentRepository.findById(id).orElse(null);
            if (a == null || !a.getDonor().getId().equals(donor.getId())
                    || "CANCELLED".equalsIgnoreCase(a.getStatus())
                    || "COMPLETED".equalsIgnoreCase(a.getStatus())) {
                return "redirect:/appointment?error=invalid";
            }
            LocalDate d = LocalDate.parse(date);
            if (d.isBefore(LocalDate.now())) return "redirect:/appointment?error=past";
            a.setAppointmentDate(d);
            a.setAppointmentTime(LocalTime.parse(time));
            a.setStatus("PENDING");
            appointmentRepository.save(a);
        } catch (Exception e) {
            return "redirect:/appointment?error=invalid";
        }
        return "redirect:/appointment?rescheduled=true";
    }

    @GetMapping("/donation-history")
    public String donationHistory(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        model.addAttribute("loggedInUser", donor);
        List<Donation> donations = donationRepository.findByDonorOrderByDonationDateDesc(donor);
        model.addAttribute("donations", donations);
        Donation latest = donations.stream()
                .filter(d -> "COMPLETED".equalsIgnoreCase(d.getStatus()) && d.getDonationDate() != null)
                .findFirst().orElse(null);
        model.addAttribute("nextEligibleDate",
                latest == null ? null : latest.getDonationDate().plusMonths(6));
        return "donation-history";
    }

    @GetMapping("/blood-requests")
    public String bloodRequests(HttpSession session, Model model) {
        User donor = currentDonor(session);
        if (donor == null) return "redirect:/login";
        List<BloodRequest> requests = donor.getBloodType() == null || donor.getBloodType().isBlank()
                ? bloodRequestRepository.findTop10ByOrderByCreatedAtDesc()
                : bloodRequestRepository.findTop10ByBloodTypeOrderByCreatedAtDesc(donor.getBloodType());
        model.addAttribute("loggedInUser", donor);
        model.addAttribute("requests", requests);
        return "blood-requests";
    }
}
