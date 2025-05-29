package com.app.pandemicnet.service.impl;

import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.Notification;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.repository.EntryRepository;
import com.app.pandemicnet.repository.NotificationRepository;
import com.app.pandemicnet.repository.UserRepository;
import com.app.pandemicnet.service.ContactTracingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactTracingServiceImpl implements ContactTracingService {

    private final UserRepository userRepository;
    private final EntryRepository entryRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void updateUserStatus(Long userId, User.Status newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(newStatus);
        userRepository.save(user);

        if (newStatus == User.Status.INFECTED) {
            // Find entries for the infected user in the last 14 days
            LocalDateTime startTime = LocalDateTime.now().minusDays(14);
            List<Entry> infectedEntries = entryRepository.findByUserIdAndTimeRange(userId, startTime);

            // Identify at-risk users
            for (Entry entry : infectedEntries) {
                LocalDateTime entryTime = entry.getTimestamp();
                List<User> atRiskUsers = entryRepository.findAtRiskUsers(
                        entry.getDataCentre().getId(),
                        entryTime.minusHours(2),
                        entryTime.plusHours(2),
                        userId
                );

                // Update status and send notifications
                for (User atRiskUser : atRiskUsers) {
                    atRiskUser.setStatus(User.Status.AT_RISK);
                    userRepository.save(atRiskUser);

                    Notification notification = new Notification();
                    notification.setUser(atRiskUser);
                    notification.setStatus(User.Status.AT_RISK);
                    notification.setMessage("You may have been exposed to an infected individual at " +
                            entry.getDataCentre().getName());
                    notificationRepository.save(notification);
                }
            }
        }
    }
}
