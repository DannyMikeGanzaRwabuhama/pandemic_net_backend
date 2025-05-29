package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.EntryRequest;
import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;
import com.app.pandemicnet.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    @PostMapping
    // @PreAuthorize("hasRole('SCANNER') or hasRole('ADMIN')")
    public ResponseEntity<Entry> createEntry(@RequestBody EntryRequest request) {
        Entry entry = entryService.createEntry(request.getUserUniqueId(), request.getDataCentreUniqueId());
        return ResponseEntity.ok(entry);
    }

    // GET /api/entries/user/{userId}?startTime=2024-06-01T00:00:00
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findEntriesByUserAndTime(
            @PathVariable(required = false)  Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME ) LocalDateTime endTime
    ) {
        List<Entry> entries;
if(userId != null && startTime != null && endTime == null) {
     entries = entryService.findEntriesByUserAndTime(userId, startTime);
     return ResponseEntity.ok(entries);
}
if(userId != null && endTime != null && startTime != null) {
    entries = entryService.findEntriesByUserAndTimeRange(userId,startTime,endTime);
    return ResponseEntity.ok(entries);

}
if(userId != null && endTime == null) {
    return ResponseEntity.ok(entryService.findByUserId(userId));
}
    return ResponseEntity.badRequest().build();
    }

    // GET /api/entries/at-risk?dataCentreId=1&startTime=2024-06-01T00:00:00&endTime=2024-06-01T23:59:59&excludeUserId=2
    @GetMapping("/at-risk")
    public ResponseEntity<List<User>> findAtRiskUsers(
            @RequestParam Long dataCentreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Long excludeUserId) {
        List<User> users = entryService.findAtRiskUsers(dataCentreId, startTime, endTime, excludeUserId);
        return ResponseEntity.ok(users);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEntry(@PathVariable Long id) {
         entryService.deleteEntry(id);
        return ResponseEntity.ok("Entry deleted");
    }


}