package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTankCycleRequest {

    @JsonProperty("tankId")
    @NotNull(message = "Tank ID is required")
    private Long assetId;

    @JsonProperty("dateDebut")
    @NotNull(message = "Start date is required")
    private LocalDateTime startedAt;

    @JsonProperty("dateLimite")
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadlineAt;
}
