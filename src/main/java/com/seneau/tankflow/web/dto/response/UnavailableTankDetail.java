package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableTankDetail {

    private String manufacturerSerial;
    private String reason;  // "CYCLE_IN_PROGRESS", "NOT_FOUND", etc.
    private Long activeCycleId;  // Si applicable
    private String additionalInfo;
}
