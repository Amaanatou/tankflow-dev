package com.seneau.tankflow.data.model;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Alerte automatique générée par le système.
 * 4 niveaux: WARNING (J-15), URGENT (J-10), CRITICAL (J-5), OVERDUE (J+0)
 */
@Entity
@Table(name = "alert")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Alert extends AbstractEntity {

    @Column(nullable = false)
    private Long cycleId;  // Cycle concerné

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertLevel level;  // WARNING, URGENT, CRITICAL, OVERDUE

    @Column(length = 50)
    private String status;  // OPEN, ACKNOWLEDGED, RESOLVED

    private Integer thresholdDays;  // Seuil de jours avant fin

    @Column(nullable = false)
    private LocalDateTime triggeredAt;  // Quand l'alerte a été générée

    private LocalDateTime resolvedAt;  // Quand l'alerte a été résolue

    private Long resolvedByUserId;  // Utilisateur qui a résolu

    @Column(columnDefinition = "TEXT")
    private String notes;  // Notes de résolution
}
