package com.app.pandemicnet.service.impl;

import com.app.pandemicnet.Exception.ResourceNotFoundException;
import com.app.pandemicnet.dto.DataLocationRequest;
import com.app.pandemicnet.dto.DataLocationResponse;
import com.app.pandemicnet.model.DataLocation;
import com.app.pandemicnet.repository.DataLocationRepository;
import com.app.pandemicnet.service.DataLocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the DataLocationService interface.
 * Provides business logic for managing data locations.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DataLocationServiceImpl implements DataLocationService {

    private static final String DATA_LOCATION_NOT_FOUND = "Data Location not found with %s: %s";

    private final DataLocationRepository dataLocationRepository;

    @Override
    @Transactional
    public DataLocationResponse createDataLocation(DataLocationRequest request) {
        log.info("Creating new data location with name: {}", request.getName());
        
        DataLocation dataLocation = new DataLocation();
        dataLocation.setName(request.getName());
        dataLocation.setLatitude(request.getLatitude());
        dataLocation.setLongitude(request.getLongitude());
        dataLocation.setAddress(request.getAddress());
        dataLocation.setUniqueId(UUID.randomUUID());
        
        DataLocation savedDataLocation = dataLocationRepository.save(dataLocation);
        log.info("Created data location with ID: {}", savedDataLocation.getId());
        
        return mapToDataLocationResponse(savedDataLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public DataLocationResponse getDataLocation(Long id) {
        log.debug("Fetching data location with ID: {}", id);
        
        DataLocation dataLocation = dataLocationRepository.findById(id)
                .orElseThrow(() -> {
                    String message = String.format(DATA_LOCATION_NOT_FOUND, "id", id);
                    log.error(message);
                    return new ResourceNotFoundException(message);
                });
                
        return mapToDataLocationResponse(dataLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public DataLocationResponse getDataLocationByUniqueId(UUID uniqueId) {
        log.debug("Fetching data location with unique ID: {}", uniqueId);
        
        DataLocation dataLocation = dataLocationRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> {
                    String message = String.format(DATA_LOCATION_NOT_FOUND, "uniqueId", uniqueId);
                    log.error(message);
                    return new ResourceNotFoundException(message);
                });
                
        return mapToDataLocationResponse(dataLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataLocationResponse> getAllDataLocations() {
        log.debug("Fetching all data locations");
        
        return dataLocationRepository.findAll().stream()
                .map(this::mapToDataLocationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DataLocationResponse updateDataLocation(Long id, DataLocationRequest request) {
        log.info("Updating data location with ID: {}", id);
        
        DataLocation dataLocation = dataLocationRepository.findById(id)
                .orElseThrow(() -> {
                    String message = String.format(DATA_LOCATION_NOT_FOUND, "id", id);
                    log.error(message);
                    return new ResourceNotFoundException(message);
                });
        
        // Update fields if they are not null in the request
        if (request.getName() != null) {
            dataLocation.setName(request.getName());
        }
        if (request.getLatitude() != null) {
            dataLocation.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            dataLocation.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            dataLocation.setAddress(request.getAddress());
        }
        
        DataLocation updatedDataLocation = dataLocationRepository.save(dataLocation);
        log.info("Updated data location with ID: {}", id);
        
        return mapToDataLocationResponse(updatedDataLocation);
    }

    @Override
    @Transactional
    public void deleteDataLocation(Long id) {
        log.info("Deleting data location with ID: {}", id);
        
        if (!dataLocationRepository.existsById(id)) {
            String message = String.format(DATA_LOCATION_NOT_FOUND, "id", id);
            log.error(message);
            throw new ResourceNotFoundException(message);
        }
        
        dataLocationRepository.deleteById(id);
        log.info("Deleted data location with ID: {}", id);
    }

    /**
     * Maps a DataLocation entity to a DataLocationResponse DTO.
     *
     * @param dataLocation the DataLocation entity to map
     * @return the mapped DataLocationResponse
     */
    private DataLocationResponse mapToDataLocationResponse(DataLocation dataLocation) {
        if (dataLocation == null) {
            return null;
        }
        
        return DataLocationResponse.builder()
                .id(dataLocation.getId())
                .uniqueId(dataLocation.getUniqueId())
                .name(dataLocation.getName())
                .latitude(dataLocation.getLatitude())
                .longitude(dataLocation.getLongitude())
                .address(dataLocation.getAddress())
                .build();
    }
}
