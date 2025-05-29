package com.app.pandemicnet.controller;



import com.app.pandemicnet.model.*;
import com.app.pandemicnet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @PostMapping
//    @PreAuthorize("hasRole('SCANNER') or hasRole('ADMIN')")
    public ResponseEntity<Entry> createEntry(@RequestBody EntryRequest request) {
        Entry entry = entryService.createEntry(request.getUserUniqueId(), request.getDataCentreUniqueId());
        return ResponseEntity.ok(entry);
    }
}

