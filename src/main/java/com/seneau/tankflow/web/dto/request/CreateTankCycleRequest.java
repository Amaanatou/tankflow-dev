package com.seneau.tankflow.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTankCycleRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Start date is required")
    private LocalDateTime startedAt;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadlineAt;
}
