package com.seneau.tankflow.data.model;

import com.seneau.tankflow.data.enumeration.CyclePosition;
import com.seneau.tankflow.data.enumeration.CycleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Un cycle/rotation d'un tank physique.
 * Représente un aller-retour complet du tank.
 * Contient 9 étapes du workflow avec deadline de 210 jours.
 */
@Entity
@Table(name = "cycle", uniqueConstraints = {
        @UniqueConstraint(columnNames = "public_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TankCycle extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String publicCode;  // Format: TANK-[N°fab]-[Année]-[CodeAléatoire]

    @Column(nullable = false)
    private Long assetId;  // Référence au tank physique

    @Column(nullable = false)
    private LocalDateTime startedAt;  // Jour 0 (expédition)

    private LocalDateTime returnedToSupplierAt;  // Jour N (clôture)

    @Column(nullable = false)
    private LocalDateTime deadlineAt;  // startedAt + 210 jours

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CycleStatus status;  // IN_PROGRESS, COMPLETED

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CyclePosition position;  // EN_TRANSIT, STOCKE, EN_UTILISATION, VIDE, HORS_CYCLE

    @Column(nullable = false)
    private Integer currentStepNumber;  // Étape actuelle (1-9)

    private Integer durationDays;  // Calculé auto

    private Integer daysRemaining;  // Calculé auto

    @Column(length = 255)
    private String localisation;  // Localisation actuelle du tank

    @Column(length = 100)
    private String zone;  // Zone actuelle

    @Column(precision = 10, scale = 2)
    private BigDecimal penaltyAmount;  // Pénalité en €

    @Column(nullable = false)
    private Boolean isPenaltyApplied;  // Pénalité acquittée

    @Version
    private Long version;  // Optimistic Locking
}
