package com.app.pandemicnet.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String street;
}
