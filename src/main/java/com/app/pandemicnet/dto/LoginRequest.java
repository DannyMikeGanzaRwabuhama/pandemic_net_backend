package com.app.pandemicnet.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    @NotEmpty(message = "phone number field should not be empty")
    private String phoneNumber;
    @NotEmpty(message = "password field should not be empty")
    private String password;

}