package com.seneau.tankflow.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankAssetResponse {

    private Long id;

    @JsonProperty("numeroFabricant")
    private String manufacturerSerial;

    @JsonProperty("fournisseur")
    private String supplierId;

    private Boolean hasSafetyBell;

    @JsonProperty("statut")
    private String tankStatus;

    private String notes;

    private List<TankCycleResponse> cycles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
