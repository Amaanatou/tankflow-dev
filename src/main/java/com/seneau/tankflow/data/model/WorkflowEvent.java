package com.seneau.tankflow.data.model;

import com.seneau.tankflow.data.enumeration.WorkflowStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evenement immuable du workflow.
 * Enregistre chaque transition d'étape du cycle.
 * Utilisé pour l'audit et l'event sourcing léger.
 */
@Entity
@Table(name = "workflow_event", uniqueConstraints = {
        @UniqueConstraint(columnNames = "idempotency_key")
})
// Note: cycleId references the 'cycle' table (renamed from tank_cycle)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowEvent extends AbstractEntity {

    @Column(nullable = false)
    private Long cycleId;

    private Long cycleStageId;

    @Column(nullable = false)
    private Integer stepNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStep eventType;  // Type d'événement

    private Long locationId;  // Lieu de l'événement

    private Long zoneId;  // Zone du lieu

    private Long performedByUserId;  // Utilisateur qui a effectué l'action

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;  // Quand l'événement s'est produit

    @Column(unique = true, length = 255)
    private String idempotencyKey;  // Pour éviter les doublons (deviceId:clientEventId)

    @Column(columnDefinition = "JSONB")
    private String metadata;  // Métadonnées JSON

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // Immutable
}
