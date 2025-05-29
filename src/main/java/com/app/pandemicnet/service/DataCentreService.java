package com.app.pandemicnet.service;

import com.app.pandemicnet.dtos.DataCentreRequest;
import com.app.pandemicnet.dtos.DataCentreResponse;
import com.app.pandemicnet.model.DataCentre;

import java.util.List;
import java.util.UUID;

public interface DataCentreService {
    DataCentreResponse createDataCentre(DataCentreRequest dataCentre);
    DataCentreResponse getDataCentre(Long id);
    DataCentreResponse getDataCentreByUniqueId(UUID uniqueId);
    List<DataCentreResponse> getAllDataCentres();
    DataCentreResponse updateDataCentre(Long id, DataCentreRequest request);
    void deleteDataCentre(Long id);

}
