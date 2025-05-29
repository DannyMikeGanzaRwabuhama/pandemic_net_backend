package com.app.pandemicnet.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String phoneNumber;
    private String password;

}