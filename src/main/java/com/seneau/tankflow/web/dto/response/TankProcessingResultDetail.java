package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankProcessingResultDetail {

    private Long tankId;
    private String manufacturerSerial;
    private String supplierId;
    private String tankStatus;
}
