package com.hotel.management.controller;

import com.hotel.management.entity.Staff;
import com.hotel.management.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staff";
    }

    @GetMapping("/new")
    public String showStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff-form";
    }

    @PostMapping("/save")
    public String saveStaff(@ModelAttribute("staff") Staff staff) {
        staffService.saveStaff(staff);
        return "redirect:/staff";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Staff staff = staffService.getAllStaff().stream()
                .filter(s -> s.getStaffId().equals(id))
                .findFirst()
                .orElse(null);
        if (staff != null) {
            model.addAttribute("staff", staff);
            return "staff-form";
        }
        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return "redirect:/staff";
    }
}
