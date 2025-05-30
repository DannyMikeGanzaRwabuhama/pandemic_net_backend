package com.app.pandemicnet.repository;

import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
    // Find entries for a user after a specific time
    @Query("SELECT e FROM Entry e WHERE e.user.id = :userId AND e.timestamp >= :startTime")
    List<Entry> findByUserIdAndTimestampAfter(Long userId, LocalDateTime startTime);
    
    // Find entries for a user within a time range
    @Query("SELECT e FROM Entry e WHERE e.user.id = :userId AND e.timestamp BETWEEN :startTime AND :endTime")
    List<Entry> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // Find all users who were at a specific location within a time window (for contact tracing)
    @Query("SELECT DISTINCT e.user FROM Entry e WHERE e.dataLocation.id = :dataLocationId " +
           "AND e.timestamp BETWEEN :startTime AND :endTime " +
           "AND (:excludeUserId IS NULL OR e.user.id != :excludeUserId)")
    List<User> findAtRiskUsers(Long dataLocationId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId);
    
    // Find all entries for a user
    @Query("SELECT e FROM Entry e WHERE e.user.id = :userId")
    List<Entry> findEntriesByUserId(Long userId);
    
    // Find entries with high temperature for a user
    @Query("SELECT e FROM Entry e WHERE e.user.id = :userId AND e.temperature > :threshold")
    List<Entry> findByUserIdAndHighTemperature(Long userId, Double threshold);
    
    // Find potential contacts for contact tracing (users who were at the same location within a time window)
    @Query("SELECT e FROM Entry e " +
           "WHERE e.dataLocation.id = :dataLocationId " +
           "AND e.timestamp BETWEEN :startTime AND :endTime " +
           "AND e.user.id != :excludeUserId")
    List<Entry> findPotentialContacts(Long dataLocationId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId);
    
    // Find connected users (users who were at the same location within 15 minutes)
    @Query(value = "SELECT DISTINCT e2.user FROM Entry e1 JOIN Entry e2 " +
           "WHERE e1.dataLocation.id = e2.dataLocation.id " +
           "AND e1.user.id = :userId " +
           "AND e2.user.id != :userId " +
           "AND ABS(TIMESTAMPDIFF(MINUTE, e1.timestamp, e2.timestamp)) <= 15",
            nativeQuery = true)
    List<User> findConnectedUsers(Long userId);
    
    // Find entry by ID
    Optional<Entry> findById(Long id);
    
    // Save an entry
    <S extends Entry> S save(S entity);
    
    // Delete an entry by ID
    void deleteById(Long id);
    
    // Check if an entry exists by ID
    boolean existsById(Long id);
}