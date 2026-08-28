package com.seneau.tankchlore_service.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "agents")
public class Agent extends AbstractEntity {
    private String fullName;
    private Long matricule;
}
