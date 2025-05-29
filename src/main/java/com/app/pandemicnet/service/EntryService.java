package com.app.pandemicnet.service;

import com.app.pandemicnet.model.*;
import com.app.pandemicnet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataCentreRepository dataCentreRepository;

    @Transactional
    public Entry createEntry(UUID userUniqueId, UUID dataCentreUniqueId) {
        User user = userRepository.findUserByUniqueId(userUniqueId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DataCentre dataCentre = dataCentreRepository.findDataCentreByUniqueId(dataCentreUniqueId);

        if (user == null || dataCentre == null) {
            throw new RuntimeException("User or Data Centre not found");
        }

        Entry entry = new Entry();
        entry.setUser(user);
        entry.setDataCentre(dataCentre);
        entry.setTimestamp(LocalDateTime.now());
        return entryRepository.save(entry);
    }

    public List<Entry> findEntriesByUserAndTime(Long userId, LocalDateTime startTime) {
        return entryRepository.findByUserIdAndTimeRange(userId, startTime);
    }

    public List<User> findAtRiskUsers(Long dataCentreId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId) {
        return entryRepository.findAtRiskUsers(dataCentreId, startTime, endTime, excludeUserId);
    }
}