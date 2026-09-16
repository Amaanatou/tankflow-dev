package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String site;

    private String zone;

    @JsonProperty("performedBy")
    private Long performedByUserId;

    private String tankCondition;

    private Boolean safetyBellPresent;

    private String documentReference;

    private String comment;

    @NotNull(message = "Idempotency key is required")
    private String idempotencyKey;

    private String metadata;
}
