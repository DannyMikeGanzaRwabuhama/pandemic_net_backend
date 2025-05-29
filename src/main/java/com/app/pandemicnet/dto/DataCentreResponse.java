package com.app.pandemicnet.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DataCentreResponse {
    private Long id;
    private UUID uniqueId;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
}
