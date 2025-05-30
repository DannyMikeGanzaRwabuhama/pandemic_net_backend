package com.app.pandemicnet.service.impl;

import com.app.pandemicnet.Exception.ResourceNotFoundException;
import com.app.pandemicnet.dto.ContactTracingResult;
import com.app.pandemicnet.dto.DegreesOfSeparationResult;
import com.app.pandemicnet.model.*;
import com.app.pandemicnet.repository.*;
import com.app.pandemicnet.service.EntryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService {
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final DataLocationRepository dataLocationRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Entry createTemperatureScan(UUID userUniqueId, UUID dataLocationUniqueId, Double temperature) {
        User user = userRepository.findUserByUniqueId(userUniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uniqueId: " + userUniqueId));

        DataLocation dataLocation = dataLocationRepository.findByUniqueId(dataLocationUniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("DataLocation not found with uniqueId: " + dataLocationUniqueId));

        if (temperature == null) {
            throw new IllegalArgumentException("Temperature is required");
        }

        // Check for fever
        if (temperature > 38.0) { // 38°C is considered a fever
            // Log or handle high temperature alert
            log.warn("High temperature detected for user {}: {}", user.getId(), temperature);

            // Update user status to AT_RISK
            if (user.getStatus() != User.Status.AT_RISK) {
                user.setStatus(User.Status.AT_RISK);
                userRepository.save(user);
            }

            // Send notification to user
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setStatus(User.Status.AT_RISK);
            notification.setMessage("Abnormal temperature detected (" + temperature + "°C) at " + dataLocation.getName() +
                    ". Please monitor your health and follow safety protocols.");
            notificationRepository.save(notification);
        }
        
        Entry entry = new Entry();
        entry.setUser(user);
        entry.setDataLocation(dataLocation);
        entry.setTemperature(temperature);
        entry.setTimestamp(LocalDateTime.now());
        
        return entryRepository.save(entry);
    }

    @Override
    public List<Entry> findEntriesByUserAndTime(Long userId, LocalDateTime startTime) {
        return entryRepository.findByUserIdAndTimestampAfter(userId, startTime);
    }

    @Override
    public List<User> findAtRiskUsers(Long dataLocationId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId) {
        return entryRepository.findAtRiskUsers(dataLocationId, startTime, endTime, excludeUserId);
    }

    @Override
    public List<Entry> findEntriesByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return entryRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime);
    }
    
    @Override
    public List<ContactTracingResult> findPotentialExposures(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        // Get all entries for the user in the time range
        List<Entry> userEntries = entryRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime);
        
        return userEntries.stream()
            .flatMap(entry -> {
                // For each location the user was at, find other users who were there within 60 minutes
                LocalDateTime windowStart = entry.getTimestamp().minusMinutes(60);
                LocalDateTime windowEnd = entry.getTimestamp().plusMinutes(60);
                
                return entryRepository.findPotentialContacts(
                    entry.getDataLocation().getId(),
                    windowStart,
                    windowEnd,
                    userId // exclude the current user
                ).stream()
                .map(contact -> new ContactTracingResult(
                    contact.getUser(),
                    contact.getTimestamp(),
                    entry.getDataLocation().getId(),
                    entry.getDataLocation().getName(),
                    (int) Math.abs(java.time.Duration.between(entry.getTimestamp(), contact.getTimestamp()).toMinutes())
                ));
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Entry> findByUserId(Long userId) {
        return entryRepository.findEntriesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DegreesOfSeparationResult findDegreesOfSeparation(
            Long sourceUserId, Long targetUserId, Integer maxDegrees, Integer timeWindowMinutes) {
        
        // Set default values if not provided
        int maxDegreesToSearch = maxDegrees != null ? maxDegrees : 6;
        int timeWindow = timeWindowMinutes != null ? timeWindowMinutes : 15;
        
        // Get the source and target users
        User sourceUser = userRepository.findById(sourceUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Source user not found with id: " + sourceUserId));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));
        
        // If source and target are the same
        if (sourceUserId.equals(targetUserId)) {
            return new DegreesOfSeparationResult(
                    sourceUser, 
                    targetUser, 
                    0, 
                    Collections.singletonList(
                            new DegreesOfSeparationResult.ConnectionPath(
                                    sourceUser, 
                                    null, 
                                    "Same user", 
                                    LocalDateTime.now()
                            )
                    )
            );
        }
        
        // Use BFS to find the shortest path between users
        Queue<BFSNode> queue = new LinkedList<>();
        Map<Long, BFSNode> visited = new HashMap<>();
        
        // Add source user to the queue
        BFSNode sourceNode = new BFSNode(sourceUser, 0, null, null, null);
        queue.add(sourceNode);
        visited.put(sourceUserId, sourceNode);
        
        while (!queue.isEmpty()) {
            BFSNode currentNode = queue.poll();
            User currentUser = currentNode.getUser();
            int currentDegree = currentNode.getDegree();
            
            // If we've reached the target user
            if (currentUser.getId().equals(targetUserId)) {
                return buildDegreesOfSeparationResult(sourceUser, targetUser, currentNode);
            }
            
            // Stop if we've reached the maximum degrees
            if (currentDegree >= maxDegreesToSearch) {
                continue;
            }
            
            // Find all users connected to the current user
            List<User> connectedUsers = entryRepository.findConnectedUsers(currentUser.getId());
            
            for (User connectedUser : connectedUsers) {
                if (!visited.containsKey(connectedUser.getId())) {
                    // Get the most recent connection between these users
                    Entry connection = findMostRecentConnection(currentUser.getId(), connectedUser.getId(), timeWindow);
                    if (connection != null) {
                        BFSNode newNode = new BFSNode(
                                connectedUser,
                                currentDegree + 1,
                                connection.getDataLocation(),
                                connection.getTimestamp(),
                                currentNode
                        );
                        queue.add(newNode);
                        visited.put(connectedUser.getId(), newNode);
                    }
                }
            }
        }
        
        // If we get here, no connection was found within maxDegrees
        return new DegreesOfSeparationResult(sourceUser, targetUser, -1, Collections.emptyList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactTracingResult> findConcurrentVisitors(
            Long userId, Integer timeWindowMinutes, LocalDateTime startTime, LocalDateTime endTime) {
        
        // Set default time window if not provided
        int timeWindow = timeWindowMinutes != null ? timeWindowMinutes : 15;
        
        // Get all entries for the user in the specified time range
        List<Entry> userEntries = entryRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime);
        
        // Find all entries at the same locations within the time window
        return userEntries.stream()
                .flatMap(entry -> {
                    LocalDateTime windowStart = entry.getTimestamp().minusMinutes(timeWindow);
                    LocalDateTime windowEnd = entry.getTimestamp().plusMinutes(timeWindow);
                    
                    // Find all entries at the same location within the time window
                    return entryRepository.findPotentialContacts(
                            entry.getDataLocation().getId(),
                            windowStart,
                            windowEnd,
                            userId // Exclude the user themselves
                    ).stream()
                    .filter(e -> !e.getUser().getId().equals(userId)) // Double-check exclusion
                    .map(contactEntry -> {
                        ContactTracingResult result = new ContactTracingResult();
                        result.setContactUser(contactEntry.getUser());
                        result.setContactTime(contactEntry.getTimestamp());
                        result.setDataLocationId(entry.getDataLocation().getId());
                        result.setDataLocationName(entry.getDataLocation().getName());
                        
                        // Calculate minutes apart
                        long minutesApart = Math.abs(Duration.between(
                                entry.getTimestamp(), 
                                contactEntry.getTimestamp()
                        ).toMinutes());
                        result.setMinutesApart((int) minutesApart);
                        
                        return result;
                    });
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Helper class for BFS traversal in degrees of separation calculation.
     */
    @Data
    private static class BFSNode {
        private final User user;
        private final int degree;
        private final DataLocation location;
        private final LocalDateTime timestamp;
        private final BFSNode previous;
    }
    
    /**
     * Helper method to build the DegreesOfSeparationResult from the BFS traversal.
     */
    private DegreesOfSeparationResult buildDegreesOfSeparationResult(
            User sourceUser, User targetUser, BFSNode targetNode) {
        
        List<DegreesOfSeparationResult.ConnectionPath> path = new ArrayList<>();
        BFSNode current = targetNode;
        
        // Reconstruct the path from target to source
        while (current != null) {
            if (current.getLocation() != null || current.getUser().equals(sourceUser)) {
                path.addFirst(new DegreesOfSeparationResult.ConnectionPath(
                        current.getUser(),
                        current.getLocation() != null ? current.getLocation().getId() : null,
                        current.getLocation() != null ? current.getLocation().getName() : "Start",
                        current.getTimestamp() != null ? current.getTimestamp() : LocalDateTime.now()
                ));
            }
            current = current.getPrevious();
        }

        assert targetNode != null;
        return new DegreesOfSeparationResult(
                sourceUser,
                targetUser,
                targetNode.getDegree(),
                path
        );
    }
    
    /**
     * Finds the most recent connection between two users within the given time window.
     */
    private Entry findMostRecentConnection(Long userId1, Long userId2, int timeWindowMinutes) {
        // Get all entries for both users
        List<Entry> user1Entries = entryRepository.findEntriesByUserId(userId1);
        List<Entry> user2Entries = entryRepository.findEntriesByUserId(userId2);
        
        Entry mostRecentConnection = null;
        long minTimeDifference = (long) timeWindowMinutes * 60 * 1000; // Convert to milliseconds
        
        // Find entries at the same location within the time window
        for (Entry entry1 : user1Entries) {
            for (Entry entry2 : user2Entries) {
                if (entry1.getDataLocation().getId().equals(entry2.getDataLocation().getId())) {
                    long timeDifference = Math.abs(Duration.between(
                            entry1.getTimestamp(), 
                            entry2.getTimestamp()
                    ).toMillis());
                    
                    if (timeDifference <= minTimeDifference) {
                        // Use the later of the two timestamps as the connection time
                        LocalDateTime connectionTime = entry1.getTimestamp().isAfter(entry2.getTimestamp()) ? 
                                entry1.getTimestamp() : entry2.getTimestamp();
                        
                        // Create a new entry to represent the connection
                        Entry connection = new Entry();
                        connection.setDataLocation(entry1.getDataLocation());
                        connection.setTimestamp(connectionTime);
                        connection.setUser(entry2.getUser());
                        
                        // Update the most recent connection if this one is more recent
                        if (mostRecentConnection == null || 
                            connectionTime.isAfter(mostRecentConnection.getTimestamp())) {
                            mostRecentConnection = connection;
                        }
                    }
                }
            }
        }
        
        return mostRecentConnection;
    }
    
    @Override
    public void deleteEntry(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entry not found with id: " + id);
        }
        entryRepository.deleteById(id);
    }
}