package com.app.pandemicnet.controller;

import com.app.pandemicnet.dtos.DataCentreRequest;
import com.app.pandemicnet.dtos.DataCentreResponse;
import com.app.pandemicnet.service.DataCentreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/data-centres")
public class DataCentreController {

    @Autowired
    private DataCentreService dataCentreService;

    @PostMapping
    public ResponseEntity<DataCentreResponse> createDataCentre(@Valid @RequestBody DataCentreRequest request) {
        DataCentreResponse response = dataCentreService.createDataCentre(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataCentreResponse> getDataCentre(@PathVariable Long id) {
        DataCentreResponse response = dataCentreService.getDataCentre(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unique/{uniqueId}")
    public ResponseEntity<DataCentreResponse> getDataCentreByUniqueId(@PathVariable UUID uniqueId) {
        DataCentreResponse response = dataCentreService.getDataCentreByUniqueId(uniqueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DataCentreResponse>> getAllDataCentres() {
        List<DataCentreResponse> responses = dataCentreService.getAllDataCentres();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataCentreResponse> updateDataCentre(@PathVariable Long id,@Valid @RequestBody DataCentreRequest request) {
        DataCentreResponse response = dataCentreService.updateDataCentre(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDataCentre(@PathVariable Long id) {
        dataCentreService.deleteDataCentre(id);
        return ResponseEntity.ok("Data Centre deleted");
    }
}
