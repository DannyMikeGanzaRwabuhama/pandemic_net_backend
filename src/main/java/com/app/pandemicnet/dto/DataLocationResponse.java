package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object representing a data location in the system.
 * Used to transfer data location information between the server and clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing data location information")
public class DataLocationResponse {

    @Schema(
            description = "Unique database identifier of the data location",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Globally unique identifier (UUID) of the data location",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID uniqueId;

    @Schema(
            description = "Name of the data location",
            example = "Main Data Center",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 255
    )
    private String name;

    @Schema(
            description = "Geographic latitude coordinate of the data location",
            example = "40.7128",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double latitude;

    @Schema(
            description = "Geographic longitude coordinate of the data location",
            example = "-74.0060",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double longitude;

    @Schema(
            description = "Physical address of the data location",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Address address;
}
