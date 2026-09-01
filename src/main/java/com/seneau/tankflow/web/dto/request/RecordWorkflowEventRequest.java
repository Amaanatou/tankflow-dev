package com.seneau.tankflow.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordWorkflowEventRequest {

    @NotNull(message = "Workflow step is required")
    private String step;

    private Long locationId;

    private Long zoneId;

    private Long performedByUserId;

    @NotNull(message = "Idempotency key is required")
    private String idempotencyKey;

    private String metadata;
}
