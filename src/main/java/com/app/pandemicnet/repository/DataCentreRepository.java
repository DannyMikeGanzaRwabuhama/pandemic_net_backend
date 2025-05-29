package com.app.pandemicnet.repository;

import com.app.pandemicnet.model.DataCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataCentreRepository extends JpaRepository<DataCentre, Long> {
    DataCentre findDataCentreByUniqueId(UUID dataCentreUniqueId);
}
