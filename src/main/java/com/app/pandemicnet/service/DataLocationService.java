package com.app.pandemicnet.service;

import com.app.pandemicnet.dto.DataLocationRequest;
import com.app.pandemicnet.dto.DataLocationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing data locations.
 * Provides CRUD operations for data locations.
 */
public interface DataLocationService {
    
    /**
     * Creates a new data location.
     *
     * @param dataLocation the data location details
     * @return the created data location
     */
    @Operation(summary = "Create a new data location")
    DataLocationResponse createDataLocation(
            @Parameter(description = "Data location details") DataLocationRequest dataLocation);

    /**
     * Retrieves a data location by its ID.
     *
     * @param id the data location ID
     * @return the data location
     */
    @Operation(summary = "Get data location by ID")
    DataLocationResponse getDataLocation(
            @Parameter(description = "ID of the data location to be retrieved") Long id);

    /**
     * Retrieves a data location by its unique identifier.
     *
     * @param uniqueId the unique identifier
     * @return the data location
     */
    @Operation(summary = "Get data location by unique ID")
    DataLocationResponse getDataLocationByUniqueId(
            @Parameter(description = "Unique ID of the data location") UUID uniqueId);

    /**
     * Retrieves all data locations.
     *
     * @return list of all data locations
     */
    @Operation(summary = "Get all data locations")
    List<DataLocationResponse> getAllDataLocations();

    /**
     * Updates an existing data location.
     *
     * @param id the data location ID
     * @param request the updated data location details
     * @return the updated data location
     */
    @Operation(summary = "Update a data location")
    DataLocationResponse updateDataLocation(
            @Parameter(description = "ID of the data location to be updated") Long id,
            @Parameter(description = "Updated data location details") DataLocationRequest request);

    /**
     * Deletes a data location by its ID.
     *
     * @param id the data location ID
     */
    @Operation(summary = "Delete a data location")
    void deleteDataLocation(
            @Parameter(description = "ID of the data location to be deleted") Long id);
}
