package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.ContactTracingResult;
import com.app.pandemicnet.dto.DegreesOfSeparationResult;
import com.app.pandemicnet.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contact-tracing")
@RequiredArgsConstructor
@Tag(name = "Contact Tracing", description = "APIs for contact tracing and exposure detection")
public class ContactTracingController {

    private final EntryService entryService;

    @GetMapping("/degrees-of-separation")
    @Operation(
            summary = "Find degrees of separation between two users",
            description = "Calculates the degrees of separation between two users based on their location history.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully calculated degrees of separation",
                            content = @Content(schema = @Schema(implementation = DegreesOfSeparationResult.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "One or both users not found"
                    )
            }
    )
    public ResponseEntity<DegreesOfSeparationResult> findDegreesOfSeparation(
            @RequestParam @Parameter(description = "ID of the source user") Long sourceUserId,
            @RequestParam @Parameter(description = "ID of the target user") Long targetUserId,
            @RequestParam(required = false) @Parameter(description = "Maximum degrees of separation to search for (default: 6)") Integer maxDegrees,
            @RequestParam(required = false) @Parameter(description = "Time window in minutes to consider for connections (default: 15)") Integer timeWindowMinutes) {
        
        DegreesOfSeparationResult result = entryService.findDegreesOfSeparation(
                sourceUserId, targetUserId, maxDegrees, timeWindowMinutes);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/concurrent-visitors")
    @Operation(
            summary = "Find users who were at the same location around the same time",
            description = "Finds all users who were at the same location as the specified user within the given time window.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully found concurrent visitors",
                            content = @Content(schema = @Schema(implementation = ContactTracingResult.class))
                    )
            }
    )
    public ResponseEntity<List<ContactTracingResult>> findConcurrentVisitors(
            @RequestParam @Parameter(description = "ID of the user to check") Long userId,
            @RequestParam(required = false) @Parameter(description = "Time window in minutes to check before and after each entry (default: 15)") Integer timeWindowMinutes,
            @RequestParam @Parameter(description = "Start time for the search (format: yyyy-MM-dd'T'HH:mm:ss)") 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @Parameter(description = "End time for the search (format: yyyy-MM-dd'T'HH:mm:ss)") 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<ContactTracingResult> results = entryService.findConcurrentVisitors(
                userId, timeWindowMinutes, startTime, endTime);
        
        return ResponseEntity.ok(results);
    }
}
