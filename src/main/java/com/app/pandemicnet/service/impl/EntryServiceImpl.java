package com.app.pandemicnet.service.impl;

import com.app.pandemicnet.Exception.ResourceNotFoundException;
import com.app.pandemicnet.model.*;
import com.app.pandemicnet.repository.*;
import com.app.pandemicnet.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EntryServiceImpl implements EntryService {

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
        DataCentre dataCentre = dataCentreRepository.findByUniqueId(dataCentreUniqueId).orElseThrow(
                () -> new ResourceNotFoundException("DataCentre not found")
        );

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

    public List<Entry> findEntriesByUserAndTimeRange(Long userId,LocalDateTime startTime,LocalDateTime endTime){
        return entryRepository.findEntriesByUserIdAndTimestampBetween(userId,startTime,endTime);

    }

    public List<Entry> findByUserId(Long userId){
        return entryRepository.findEntriesByUserId(userId);
    }

    public void deleteEntry(Long id)
    {
      entryRepository.deleteById(id);
    }
}