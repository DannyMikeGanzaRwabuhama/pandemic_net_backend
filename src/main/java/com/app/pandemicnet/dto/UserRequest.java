package com.app.pandemicnet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {
    private String phoneNumber;
    private String password;

}