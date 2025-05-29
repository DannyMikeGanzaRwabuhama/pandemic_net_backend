package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.LoginRequest;
import com.app.pandemicnet.dto.RegisterRequest;
import com.app.pandemicnet.dto.StatusUpdateRequest;
import com.app.pandemicnet.model.Notification;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.service.ContactTracingService;
import com.app.pandemicnet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactTracingService contactTracingService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.loginUser(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserStatus(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        contactTracingService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok("Status updated and notifications sent");
    }

    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserNotifications(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
