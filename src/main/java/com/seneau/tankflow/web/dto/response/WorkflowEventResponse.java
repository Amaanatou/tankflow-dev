package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEventResponse {

    private Long id;

    private Long cycleId;

    private Integer stepNumber;

    private String eventType;

    private String site;

    private String zone;

    private Long performedByUserId;

    private LocalDateTime eventTimestamp;

    private String tankCondition;

    private Boolean safetyBellPresent;

    private String documentReference;

    private String comment;

    private String idempotencyKey;

    private String metadata;

    private LocalDateTime createdAt;
}
