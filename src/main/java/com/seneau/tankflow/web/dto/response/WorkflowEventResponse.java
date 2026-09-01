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

    private Long cycleStageId;

    private Integer stepNumber;

    private String eventType;

    private Long locationId;

    private Long zoneId;

    private Long performedByUserId;

    private LocalDateTime eventTimestamp;

    private String idempotencyKey;

    private String metadata;

    private LocalDateTime createdAt;
}
