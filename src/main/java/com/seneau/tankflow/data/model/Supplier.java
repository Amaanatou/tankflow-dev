package com.seneau.tankflow.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Fournisseur de tanks.
 * Identifie le fabricant d'origine de chaque tank.
 */
@Entity
@Table(name = "supplier", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Supplier extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;  // Nom du fournisseur

    @Column(length = 255)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(nullable = false)
    private Boolean isActive;  // Fournisseur actif
}
