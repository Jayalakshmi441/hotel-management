package com.hotel.management.controller;

import com.hotel.management.entity.Room;
import com.hotel.management.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "rooms";
    }

    @GetMapping("/new")
    public String showRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("types", Room.RoomType.values());
        model.addAttribute("statuses", Room.RoomStatus.values());
        return "room-form";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") Room room, Model model, RedirectAttributes redirectAttributes) {
        try {
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Room " + room.getRoomNumber() + " saved successfully!");
            return "redirect:/rooms";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving room: " + e.getMessage());
            model.addAttribute("room", room);
            model.addAttribute("types", Room.RoomType.values());
            model.addAttribute("statuses", Room.RoomStatus.values());
            return "room-form";
        }
    }

    @GetMapping("/edit-unit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println(">>> TRACE: Entering Edit Room unit for ID: " + id);
        Room room = roomService.getRoomById(id);
        if (room != null) {
            model.addAttribute("room", room);
            model.addAttribute("types", Room.RoomType.values());
            model.addAttribute("statuses", Room.RoomStatus.values());
            return "room-form";
        }
        return "redirect:/rooms?error=NotFound";
    }

    @PostMapping("/delete-unit/{id}")
    public String deleteRoom(@PathVariable Long id) {
        System.out.println(">>> TRACE: Entering Delete Room unit for ID: " + id);
        roomService.deleteRoom(id);
        return "redirect:/rooms?success=Deleted";
    }
}
