package com.seneau.tankflow.data.model;

import com.seneau.tankflow.data.enumeration.ExpeditionStatus;
import com.seneau.tankflow.data.enumeration.ExpeditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Expédition - Regroupement logistique de cycles de tanks.
 * Une expédition peut contenir plusieurs cycles en cours différents.
 */
@Entity
@Table(name = "expedition", uniqueConstraints = {
        @UniqueConstraint(columnNames = "reference")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Expedition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpeditionType type;

    @Column(nullable = false, length = 255)
    private String origine;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(length = 255)
    private String transporteur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpeditionStatus statut;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    private LocalDateTime dateArrivee;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
