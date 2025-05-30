package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.ContactTracingResult;
import com.app.pandemicnet.dto.EntryRequest;
import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
@Tag(name = "Entry Management", description = "APIs for managing entries and contact tracing")
public class EntryController {
    private final EntryService entryService;

    @PostMapping("/scan")
    @Operation(summary = "Record a temperature scan")
    @PreAuthorize("hasRole('SCANNER') or hasRole('ADMIN')")
    public ResponseEntity<Entry> recordTemperatureScan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Temperature scan details")
            @RequestBody EntryRequest request) {
        Entry entry = entryService.createTemperatureScan(
                request.getUserUniqueId(),
                request.getDataLocationUniqueId(),
                request.getTemperature()
        );
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get entries for a user within a time range")
    public ResponseEntity<?> findEntriesByUserAndTime(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start time (ISO format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time (ISO format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime != null && endTime != null) {
            return ResponseEntity.ok(entryService.findEntriesByUserAndTimeRange(userId, startTime, endTime));
        } else if (startTime != null) {
            return ResponseEntity.ok(entryService.findEntriesByUserAndTime(userId, startTime));
        } else {
            return ResponseEntity.ok(entryService.findByUserId(userId));
        }
    }

    @GetMapping("/contact-tracing/{userId}")
    @Operation(summary = "Find potential exposures for a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactTracingResult>> findPotentialExposures(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        return ResponseEntity.ok(entryService.findPotentialExposures(userId, startTime, endTime));
    }

    @GetMapping("/at-risk/{dataLocationId}")
    @Operation(summary = "Find users at risk at a specific location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> findAtRiskUsers(
            @Parameter(description = "Data Location ID", required = true)
            @PathVariable Long dataLocationId,
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "User ID to exclude")
            @RequestParam(required = false) Long excludeUserId) {
        
        return ResponseEntity.ok(entryService.findAtRiskUsers(dataLocationId, startTime, endTime, excludeUserId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}