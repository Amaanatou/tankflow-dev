package com.seneau.tankflow.data.model;

import com.seneau.tankflow.data.enumeration.TankCondition;
import com.seneau.tankflow.data.enumeration.WorkflowStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowEvent extends AbstractEntity {

    @Column(nullable = false)
    private Long cycleId;

    @Column(nullable = false)
    private Integer stepNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStep eventType;

    @Column(length = 255)
    private String site;

    @Column(length = 100)
    private String zone;

    private Long performedByUserId;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TankCondition tankCondition;

    @Column
    private Boolean safetyBellPresent;

    @Column(length = 255)
    private String documentReference;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(unique = true, length = 255)
    private String idempotencyKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    private String metadata;
}
