package com.hotel.management.controller;

import com.hotel.management.entity.Guest;
import com.hotel.management.entity.User;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            Model model) {

        // Check if user already exists
        if (userRepository.findByUsername(email).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // Validate phone number length
        if (phone == null || phone.length() != 10) {
            model.addAttribute("error", "Phone number must be exactly 10 digits!");
            return "register";
        }

        if (!phone.matches("\\d+")) {
            model.addAttribute("error", "Phone number must contain only digits!");
            return "register";
        }

        // Create User account (Security)
        User user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        // Create Guest profile (Business Data)
        Guest guest = new Guest();
        guest.setName(name);
        guest.setEmail(email);
        guest.setPhone(phone);
        guest.setIdProof("Not Provided"); // Placeholder
        guestRepository.save(guest);

        return "redirect:/login?registered=true";
    }
}
