package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusUpdateRequest {
    private User.Status status;

}