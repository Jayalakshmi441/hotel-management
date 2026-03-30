package com.hotel.management.controller;

import com.hotel.management.entity.Guest;
import com.hotel.management.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping
    public String listGuests(Model model) {
        model.addAttribute("guests", guestService.getAllGuests());
        return "guests";
    }

    @GetMapping("/new")
    public String showGuestForm(Model model) {
        model.addAttribute("guest", new Guest());
        return "guest-form";
    }

    @PostMapping("/save")
    public String saveGuest(@ModelAttribute("guest") Guest guest, RedirectAttributes redirectAttributes) {
        try {
            guestService.saveGuest(guest);
            redirectAttributes.addFlashAttribute("success", "Guest profile saved successfully.");
            return "redirect:/guests";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            if (guest.getGuestId() != null) {
                return "redirect:/guests/edit-profile/" + guest.getGuestId();
            }
            return "redirect:/guests/new";
        }
    }

    @GetMapping("/edit-profile/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println(">>> TRACE: Entering Edit Guest profile for ID: " + id);
        Guest guest = guestService.getGuestById(id);
        if (guest != null) {
            model.addAttribute("guest", guest);
            return "guest-form";
        }
        return "redirect:/guests?error=NotFound";
    }

    @PostMapping("/delete-profile/{id}")
    public String deleteGuest(@PathVariable Long id) {
        System.out.println(">>> TRACE: Entering Delete Guest profile for ID: " + id);
        guestService.deleteGuest(id);
        return "redirect:/guests?success=Deleted";
    }
}
