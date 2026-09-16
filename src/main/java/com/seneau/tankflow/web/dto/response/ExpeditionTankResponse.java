package com.seneau.tankflow.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpeditionTankResponse {

    private Long id;

    private Long expeditionId;

    private Long cycleId;

    private Long tankId;

    @JsonProperty("numeroFabricant")
    private String manufacturerSerial;

    @JsonProperty("cycleCode")
    private String publicCode;

    private Boolean selected;

    private LocalDateTime createdAt;
}
