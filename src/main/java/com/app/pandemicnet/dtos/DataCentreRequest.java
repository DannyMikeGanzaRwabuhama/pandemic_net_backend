package com.app.pandemicnet.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class DataCentreRequest {
    @NotEmpty(message = "Name field should not be empty")
    private String name;

    @NotNull
    @Min(1)
    @Max(3)
    private Double latitude;

    @NotNull
    @Min(28)
    @Max(31)
    private Double longitude;

    @NotEmpty(message = "address should not be empty")
    private String address;
}

