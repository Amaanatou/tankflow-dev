package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankAssetResponse {

    private Long id;

    private String manufacturerSerial;

    private String supplierId;

    private Boolean hasSafetyBell;

    private String tankStatus;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
