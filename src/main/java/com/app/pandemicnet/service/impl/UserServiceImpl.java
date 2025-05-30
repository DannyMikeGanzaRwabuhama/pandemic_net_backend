package com.app.pandemicnet.service.impl;

import com.app.pandemicnet.Exception.ResourceNotFoundException;
import com.app.pandemicnet.model.Notification;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.repository.NotificationRepository;
import com.app.pandemicnet.repository.UserRepository;
import com.app.pandemicnet.service.UserService;
import com.app.pandemicnet.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final JwtService jwtService;

    @Override
    public User registerUser(String phoneNumber, String password, String role) {
        if (phoneNumber == null || phoneNumber.isBlank() || password == null || password.isBlank()) {
            // 400 Bad Request: Missing required fields
            throw new IllegalArgumentException("Phone number and password must not be blank");
        }
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            // 400 Bad Request: Phone number already exists
            throw new IllegalArgumentException("Phone number already exists");
        }
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role == null || role.isBlank() ? "USER" : role);
        user.setStatus(User.Status.HEALTHY);
        return userRepository.save(user);
    }

    @Override
    public String loginUser(String phoneNumber, String password) {
        if (phoneNumber == null || phoneNumber.isBlank() || password == null || password.isBlank()) {
            // 400 Bad Request: Missing required fields
            throw new IllegalArgumentException("Phone number and password must not be blank");
        }
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by phone number"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            // 401 Unauthorized: Invalid credentials
            throw new SecurityException("Invalid credentials");
        }
        return jwtService.generateToken(user.getId(), user.getRole());
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id"));
    }

    @Override
    public User getUserByUniqueId(UUID uniqueId) {
        if (uniqueId == null) {
            throw new IllegalArgumentException("Unique id must not be null");
        }
        return userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by unique id"));
    }

    @Override
    public String getUserStatus(Long id) {
        User user = getUserById(id);
        return user.getStatus().toString();
    }

    @Override
    public List<Notification> getUserNotifications(Long id) {
        // Optionally, check if user exists before fetching notifications
        getUserById(id);
        return notificationRepository.findByUserId(id);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found by id");
        }
        userRepository.deleteById(id);
    }
}
