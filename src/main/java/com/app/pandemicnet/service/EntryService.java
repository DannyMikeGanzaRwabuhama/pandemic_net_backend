package com.app.pandemicnet.service;

import com.app.pandemicnet.dto.ContactTracingResult;
import com.app.pandemicnet.dto.DegreesOfSeparationResult;
import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing entry-related operations including temperature scanning,
 * contact tracing, and degrees of separation analysis.
 */
public interface EntryService {
    // Temperature scanning
    Entry createTemperatureScan(UUID userUniqueId, UUID dataLocationUniqueId, Double temperature);
    
    // Entry history
    List<Entry> findEntriesByUserAndTime(Long userId, LocalDateTime startTime);
    List<Entry> findEntriesByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    List<Entry> findByUserId(Long userId);
    
    // Contact tracing
    List<ContactTracingResult> findPotentialExposures(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    List<User> findAtRiskUsers(Long dataLocationId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId);
    
    // Six Degrees of Separation
    /**
     * Finds the degrees of separation between two users based on their location history.
     *
     * @param sourceUserId The ID of the source user
     * @param targetUserId The ID of the target user
     * @param maxDegrees The maximum degrees of separation to search for (default 6)
     * @param timeWindowMinutes The time window in minutes to consider for connections (default 15)
     * @return DegreesOfSeparationResult containing the separation details and path if found
     */
    DegreesOfSeparationResult findDegreesOfSeparation(
            Long sourceUserId, 
            Long targetUserId, 
            Integer maxDegrees,
            Integer timeWindowMinutes);
    
    // Minute-Based Arrival
    /**
     * Finds all users who were at the same location as the specified user within the given time window.
     *
     * @param userId The ID of the user to check
     * @param timeWindowMinutes The time window in minutes to check before and after each entry
     * @param startTime The start time to search from
     * @param endTime The end time to search to
     * @return List of ContactTracingResult with details of potential exposures
     */
    List<ContactTracingResult> findConcurrentVisitors(
            Long userId, 
            Integer timeWindowMinutes,
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    // Admin operations
    void deleteEntry(Long id);
}
