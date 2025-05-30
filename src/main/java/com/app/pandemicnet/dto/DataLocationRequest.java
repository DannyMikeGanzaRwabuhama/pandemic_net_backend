package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object for creating or updating a data location.
 * Contains all necessary information to identify and locate a physical or logical data location.
 */
@Data
@Schema(description = "Request object for creating or updating a data location")
public class DataLocationRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(
            description = "The name of the data location",
            example = "Main Data Center",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 255
    )
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Schema(
            description = "Geographic latitude coordinate of the data location",
            example = "40.7128",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Schema(
            description = "Geographic longitude coordinate of the data location",
            example = "-74.0060",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double longitude;

    @Valid
    @NotNull(message = "Address is required")
    @Schema(
            description = "Physical address of the data location",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Address address;
}
