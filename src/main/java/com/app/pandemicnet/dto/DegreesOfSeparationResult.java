package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the result of a degrees of separation query between two users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DegreesOfSeparationResult {
    private User sourceUser;
    private User targetUser;
    private int degrees;
    private List<ConnectionPath> path;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionPath {
        private User user;
        private Long dataLocationId;
        private String dataLocationName;
        private LocalDateTime timestamp;
    }
}
