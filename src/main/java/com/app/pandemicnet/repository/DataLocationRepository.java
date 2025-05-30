package com.app.pandemicnet.repository;

import com.app.pandemicnet.model.DataLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataLocationRepository extends JpaRepository<DataLocation, Long> {
    Optional<DataLocation> findByUniqueId(UUID uniqueId);
}
