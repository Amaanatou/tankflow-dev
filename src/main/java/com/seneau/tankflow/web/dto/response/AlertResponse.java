package com.seneau.tankflow.web.dto.response;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import com.seneau.tankflow.data.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour une alerte.
 * Expose les informations publiques d'une alerte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private Long id;
    private Long cycleId;
    private AlertLevel level;  // WARNING, URGENT, CRITICAL, OVERDUE
    private String status;  // OPEN, ACKNOWLEDGED, RESOLVED
    private Integer thresholdDays;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private Long resolvedByUserId;
    private String notes;
    private LocalDateTime createdAt;

    /**
     * Crée un DTO à partir d'une entité.
     */
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getCycleId(),
                alert.getLevel(),
                alert.getStatus(),
                alert.getThresholdDays(),
                alert.getTriggeredAt(),
                alert.getResolvedAt(),
                alert.getResolvedByUserId(),
                alert.getNotes(),
                alert.getCreatedAt()
        );
    }
}
