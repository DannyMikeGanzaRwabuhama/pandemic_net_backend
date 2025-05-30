package com.app.pandemicnet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class EntryRequest {
    private UUID userUniqueId;
    private UUID dataLocationUniqueId;
    private Double temperature;

}