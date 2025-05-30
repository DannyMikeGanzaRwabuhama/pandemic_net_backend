package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.LoginRequest;
import com.app.pandemicnet.dto.RegisterRequest;
import com.app.pandemicnet.dto.StatusUpdateRequest;
import com.app.pandemicnet.model.Notification;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.service.ContactTracingService;
import com.app.pandemicnet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ContactTracingService contactTracingService;

    @Operation(summary = "Register a new user", description = "Creates a user with phone number and password")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        String role = (request.getRole() == null || request.getRole().isBlank()) ? "USER" : request.getRole();
        User user = userService.registerUser(request.getPhoneNumber(), request.getPassword(), role);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.loginUser(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Get user status", description = "Retrieves the health status of a user")
    @ApiResponse(responseCode = "200", description = "Status retrieved")
    @GetMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserStatus(id));
    }

    @Operation(summary = "Update user status", description = "Updates user status and triggers contact tracing")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        contactTracingService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok("Status updated and notifications sent");
    }

    @Operation(summary = "Get user notifications", description = "Retrieves notifications for a user")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved")
    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserNotifications(id));
    }

    @Operation(summary = "Delete user account", description = "Deletes a user account for GDPR compliance")
    @ApiResponse(responseCode = "200", description = "User deleted")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @Operation(summary = "Get user by unique ID", description = "Retrieves user details by uniqueId for QR scanning")
    @ApiResponse(responseCode = "200", description = "User retrieved")
    @GetMapping("/unique/{uniqueId}")
    @PreAuthorize("hasRole('SCANNER') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable UUID uniqueId) {
        User user = userService.getUserByUniqueId(uniqueId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
