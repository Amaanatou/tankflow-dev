package com.seneau.tankflow.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepDurationDetail {

    private Integer stepNumber;
    private String stepLabel;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;  // null si en cours

    private Double durationDays;  // Calcul décimal
    private Integer durationDaysRounded;
    private String status;  // "COMPLETED" ou "IN_PROGRESS"
}
