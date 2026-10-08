package com.lifelink.lifelink.controller;
import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.*;
import com.lifelink.lifelink.repository.AppointmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {
 private final UserRepository users; private final AppointmentRepository appointments;
 public AdminAppointmentController(UserRepository users, AppointmentRepository appointments){this.users=users;this.appointments=appointments;}
 private User admin(HttpSession s){Object x=s.getAttribute("adminLoggedInUser"); if(!(x instanceof User u)||!"ADMIN".equalsIgnoreCase(u.getRole()))return null; return users.findById(u.getId()).orElse(null);}
 @GetMapping public String list(HttpSession s,Model m){User a=admin(s);if(a==null)return "redirect:/admin-login";m.addAttribute("loggedInUser",a);m.addAttribute("appointments",appointments.findByAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(LocalDate.now(),List.of("PENDING","APPROVED","COMPLETED")));return "admin-appointments";}
 @PostMapping("/{id}/status") public String status(@PathVariable Long id,@RequestParam String status,HttpSession s){
  if(admin(s)==null)return "redirect:/admin-login";
  String normalized=status==null?"":status.trim().toUpperCase();
  if(!List.of("PENDING","APPROVED","COMPLETED","CANCELLED").contains(normalized)) return "redirect:/admin/appointments?error=invalid";
  appointments.findById(id).ifPresent(a->{a.setStatus(normalized);appointments.save(a);});
  return "redirect:/admin/appointments";
 }
}
