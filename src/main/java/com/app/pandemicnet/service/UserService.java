package com.app.pandemicnet.service;

import com.app.pandemicnet.model.Notification;
import com.app.pandemicnet.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User registerUser(String phoneNumber, String password, String role);
    String loginUser(String phoneNumber, String password);
    User getUserById(Long id);
    User getUserByUniqueId(UUID uniqueId);
    String getUserStatus(Long id);
    List<Notification> getUserNotifications(Long id);
    void deleteUser(Long id);
}
