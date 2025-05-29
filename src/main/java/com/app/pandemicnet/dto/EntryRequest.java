package com.app.pandemicnet.dto;

import java.util.UUID;

public class EntryRequest {
    private UUID userUniqueId;
    private UUID dataCentreUniqueId;

    public UUID getUserUniqueId() { return userUniqueId; }
    public void setUserUniqueId(UUID userUniqueId) { this.userUniqueId = userUniqueId; }
    public UUID getDataCentreUniqueId() { return dataCentreUniqueId; }
    public void setDataCentreUniqueId(UUID dataCentreUniqueId) { this.dataCentreUniqueId = dataCentreUniqueId; }
}