package com.app.pandemicnet.service.impl;


import com.app.pandemicnet.Exception.ResourceNotFoundException;
import com.app.pandemicnet.dto.DataCentreRequest;
import com.app.pandemicnet.dto.DataCentreResponse;
import com.app.pandemicnet.model.DataCentre;
import com.app.pandemicnet.repository.DataCentreRepository;
import com.app.pandemicnet.service.DataCentreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataCentreServiceImpl implements DataCentreService {

    private DataCentreRepository dataCentreRepository;

    @Transactional
    public DataCentreResponse createDataCentre(DataCentreRequest request) {
        DataCentre dataCentre = new DataCentre();
        dataCentre.setName(request.getName());
        dataCentre.setLatitude(request.getLatitude());
        dataCentre.setLongitude(request.getLongitude());
        dataCentre.setAddress(request.getAddress());
        dataCentre.setUniqueId(UUID.randomUUID());
        DataCentre savedDataCentre = dataCentreRepository.save(dataCentre);
        return mapToDataCentreResponse(savedDataCentre);
    }

    public DataCentreResponse getDataCentre(Long id) {
        DataCentre dataCentre = dataCentreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data Centre not found"));
        return mapToDataCentreResponse(dataCentre);
    }

    public DataCentreResponse getDataCentreByUniqueId(UUID uniqueId) {
        DataCentre dataCentre = dataCentreRepository.findByUniqueId(uniqueId).orElseThrow(() ->
                new ResourceNotFoundException("Data Centre not found"));

        return mapToDataCentreResponse(dataCentre);
    }

    public List<DataCentreResponse> getAllDataCentres() {
        return dataCentreRepository.findAll().stream()
                .map(this::mapToDataCentreResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DataCentreResponse updateDataCentre(Long id, DataCentreRequest request) {
        DataCentre dataCentre = dataCentreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Data Centre not found"));
        dataCentre.setName(request.getName());
        dataCentre.setLatitude(request.getLatitude());
        dataCentre.setLongitude(request.getLongitude());
        dataCentre.setAddress(request.getAddress());
        DataCentre updatedDataCentre = dataCentreRepository.save(dataCentre);
        return mapToDataCentreResponse(updatedDataCentre);
    }

    @Transactional
    public void deleteDataCentre(Long id) {
        if (!dataCentreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Data Centre not found");
        }
        dataCentreRepository.deleteById(id);
    }

    private DataCentreResponse mapToDataCentreResponse(DataCentre dataCentre) {
        DataCentreResponse response = new DataCentreResponse();
        response.setId(dataCentre.getId());
        response.setUniqueId(dataCentre.getUniqueId());
        response.setName(dataCentre.getName());
        response.setLatitude(dataCentre.getLatitude());
        response.setLongitude(dataCentre.getLongitude());
        response.setAddress(dataCentre.getAddress());
        return response;
    }
}