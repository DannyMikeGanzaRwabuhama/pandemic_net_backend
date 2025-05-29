package com.app.pandemicnet.service;

import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EntryService {
    Entry createEntry(UUID userUniqueId, UUID dataCentreUniqueId);
    List<Entry> findEntriesByUserAndTime(Long userId, LocalDateTime startTime);
    List<User> findAtRiskUsers(Long dataCentreId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId);
    List<Entry> findEntriesByUserAndTimeRange(Long userId,LocalDateTime startTime,LocalDateTime endTime);

    List<Entry> findByUserId(Long userId);

    void deleteEntry(Long id);
}
