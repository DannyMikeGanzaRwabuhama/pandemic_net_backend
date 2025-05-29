package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RegisterRequest {
    private String phoneNumber;
    private String password;

}