package com.app.pandemicnet.service;

import com.app.pandemicnet.model.User;

public interface ContactTracingService {
    void updateUserStatus(Long userId, User.Status newStatus);
}
