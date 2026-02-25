package com.quotation.controller;

import com.quotation.model.Notification;
import com.quotation.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173") // Allow React to connect
public class NotificationController {
    @Autowired
    private NotificationService service;

    @GetMapping("/admin")
    public List<Notification> getAdminNotifications() {
        return service.getNotifications("ADMIN");
    }

    @GetMapping("/employee/{empId}")
    public List<Notification> getEmployeeNotifications(@PathVariable String empId) {
        return service.getNotifications(empId);
    }

    @PostMapping("/mark-read/{recipientId}")
    public void markRead(@PathVariable String recipientId) {
        service.markAllAsRead(recipientId);
    }
}