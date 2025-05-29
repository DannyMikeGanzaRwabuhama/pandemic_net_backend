package com.app.pandemicnet.service.impl;

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
    public User registerUser(String phoneNumber, String password) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setUniqueId(UUID.randomUUID());
        user.setRole("USER");
        user.setStatus(User.Status.HEALTHY);
        return userRepository.save(user);
    }

    @Override
    public String loginUser(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found by phone number"));
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtService.generateToken(user.getId(), user.getRole());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by id"));
    }

    @Override
    public User getUserByUniqueId(UUID uniqueId) {
        return userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("User not found by unique id"));
    }

    @Override
    public String getUserStatus(Long id) {
        User user = getUserById(id);
        return user.getStatus().toString();
    }

    @Override
    public List<Notification> getUserNotifications(Long id) {
        return notificationRepository.findByUserId(id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
