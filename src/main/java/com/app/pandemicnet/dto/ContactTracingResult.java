package com.app.pandemicnet.dto;

import com.app.pandemicnet.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactTracingResult {
    private User contactUser;
    private LocalDateTime contactTime;
    private Long dataLocationId;
    private String dataLocationName;
    private Integer minutesApart;
}
