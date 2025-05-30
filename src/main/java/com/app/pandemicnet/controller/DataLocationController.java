package com.app.pandemicnet.controller;

import com.app.pandemicnet.dto.DataLocationRequest;
import com.app.pandemicnet.dto.DataLocationResponse;
import com.app.pandemicnet.service.DataLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/data-locations")
@RequiredArgsConstructor
@Tag(name = "Data Location Management", description = "APIs for managing data locations")
public class DataLocationController {
    private final DataLocationService dataLocationService;

    @PostMapping
    @Operation(summary = "Create a new data location", 
               description = "Creates a new data location with the provided details")
    public ResponseEntity<DataLocationResponse> createDataLocation(
            @Valid @RequestBody DataLocationRequest request) {
        DataLocationResponse response = dataLocationService.createDataLocation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get data location by ID", 
               description = "Retrieves a data location by its unique identifier")
    public ResponseEntity<DataLocationResponse> getDataLocation(@PathVariable Long id) {
        DataLocationResponse response = dataLocationService.getDataLocation(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unique/{uniqueId}")
    @Operation(summary = "Get data location by unique ID", 
               description = "Retrieves a data location by its unique identifier")
    public ResponseEntity<DataLocationResponse> getDataLocationByUniqueId(@PathVariable UUID uniqueId) {
        DataLocationResponse response = dataLocationService.getDataLocationByUniqueId(uniqueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all data locations", 
               description = "Retrieves a list of all data locations")
    public ResponseEntity<List<DataLocationResponse>> getAllDataLocations() {
        List<DataLocationResponse> responses = dataLocationService.getAllDataLocations();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update data location", 
               description = "Updates an existing data location with the provided details")
    public ResponseEntity<DataLocationResponse> updateDataLocation(
            @PathVariable Long id,
            @Valid @RequestBody DataLocationRequest request) {
        DataLocationResponse response = dataLocationService.updateDataLocation(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete data location", 
               description = "Deletes a data location by its ID")
    public ResponseEntity<String> deleteDataLocation(@PathVariable Long id) {
        dataLocationService.deleteDataLocation(id);
        return ResponseEntity.ok("Data location deleted successfully");
    }
}
