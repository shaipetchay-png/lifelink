package com.lifelink.lifelink.controller;
import com.lifelink.lifelink.controller.model.repository.UserRepository;
import com.lifelink.lifelink.model.*;
import com.lifelink.lifelink.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller @RequestMapping("/admin/donations")
public class AdminDonationController {
 private final UserRepository users; private final DonationRepository donations; private final BloodInventoryRepository inventory;
 public AdminDonationController(UserRepository u,DonationRepository d,BloodInventoryRepository i){users=u;donations=d;inventory=i;}
 private User admin(HttpSession s){Object x=s.getAttribute("adminLoggedInUser");if(!(x instanceof User u)||!"ADMIN".equalsIgnoreCase(u.getRole()))return null;return users.findById(u.getId()).orElse(null);}
 @GetMapping public String list(HttpSession s,Model m){User a=admin(s);if(a==null)return "redirect:/admin-login";m.addAttribute("loggedInUser",a);m.addAttribute("donors",users.findByRoleIgnoreCaseOrderByIdDesc("DONOR"));m.addAttribute("donations",donations.findAll());m.addAttribute("inventory",inventory.findAll());return "admin-donations";}
 @PostMapping("/record") public String record(@RequestParam Long donorId,@RequestParam String donationDate,@RequestParam Integer units,HttpSession s){if(admin(s)==null)return "redirect:/admin-login";User d=users.findById(donorId).orElse(null);if(d==null||units==null||units<1)return "redirect:/admin/donations?error=invalid";Donation x=new Donation();x.setDonor(d);x.setDonationDate(LocalDate.parse(donationDate));x.setBloodType(d.getBloodType());x.setUnits(units);x.setStatus("COMPLETED");donations.save(x);if(x.getBloodType()!=null){BloodInventory bi=inventory.findByBloodType(x.getBloodType()).orElseGet(()->{BloodInventory n=new BloodInventory();n.setBloodType(x.getBloodType());n.setUnits(0);return n;});bi.setUnits(bi.getUnits()+units);inventory.save(bi);}return "redirect:/admin/donations?saved=true";}
 @PostMapping("/{id}/status")
 public String status(@PathVariable Long id,@RequestParam String status,HttpSession s){
  if(admin(s)==null)return "redirect:/admin-login";
  String normalized=status==null?"":status.trim().toUpperCase();
  if(!List.of("PENDING","COMPLETED","CANCELLED").contains(normalized)) return "redirect:/admin/donations?error=invalid";
  donations.findById(id).ifPresent(d->{
   String old=d.getStatus()==null?"":d.getStatus().toUpperCase();
   if(!old.equals(normalized)){
    int units=d.getUnits()==null?0:d.getUnits();
    if(d.getBloodType()!=null && units>0){
     BloodInventory bi=inventory.findByBloodType(d.getBloodType()).orElseGet(()->{
      BloodInventory n=new BloodInventory(); n.setBloodType(d.getBloodType()); n.setUnits(0); return n;
     });
     int current=bi.getUnits()==null?0:bi.getUnits();
     if("COMPLETED".equals(normalized) && !"COMPLETED".equals(old)) bi.setUnits(current+units);
     if("COMPLETED".equals(old) && !"COMPLETED".equals(normalized)) bi.setUnits(Math.max(0,current-units));
     inventory.save(bi);
    }
   }
   d.setStatus(normalized);
   donations.save(d);
  });
  return "redirect:/admin/donations";
 }
}
