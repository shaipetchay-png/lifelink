package com.lifelink.lifelink.controller;
import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.*;
import com.lifelink.lifelink.repository.BloodRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin/blood-requests")
public class AdminBloodRequestController {
 private final UserRepository users; private final BloodRequestRepository requests;
 public AdminBloodRequestController(UserRepository u,BloodRequestRepository r){users=u;requests=r;}
 private User admin(HttpSession s){Object x=s.getAttribute("adminLoggedInUser");if(!(x instanceof User u)||!"ADMIN".equalsIgnoreCase(u.getRole()))return null;return users.findById(u.getId()).orElse(null);}
 @GetMapping public String list(HttpSession s,Model m){User a=admin(s);if(a==null)return "redirect:/admin-login";m.addAttribute("loggedInUser",a);m.addAttribute("requests",requests.findTop10ByOrderByCreatedAtDesc());return "admin-blood-requests";}
 @PostMapping("/create") public String create(@RequestParam String patientName,@RequestParam String bloodType,@RequestParam Integer units,@RequestParam String hospital,@RequestParam(defaultValue="NORMAL") String priority,HttpSession s){if(admin(s)==null)return "redirect:/admin-login";BloodRequest r=new BloodRequest();r.setPatientName(patientName.trim());r.setBloodType(bloodType);r.setUnits(units);r.setHospital(hospital.trim());r.setPriority(priority);r.setStatus("PENDING");requests.save(r);return "redirect:/admin/blood-requests";}
 @PostMapping("/{id}/status") public String status(@PathVariable Long id,@RequestParam String status,HttpSession s){
  if(admin(s)==null)return "redirect:/admin-login";
  String normalized=status==null?"":status.trim().toUpperCase();
  if(!java.util.List.of("PENDING","APPROVED","COMPLETED","CANCELLED").contains(normalized)) return "redirect:/admin/blood-requests?error=invalid";
  requests.findById(id).ifPresent(r->{r.setStatus(normalized);requests.save(r);});
  return "redirect:/admin/blood-requests";
 }
}
