package com.seneau.tankflow.data.enumeration;

public enum TankCondition {
    BON("Bon état"),
    DEGRADÉ("Dégradé"),
    DÉFAILLANT("Défaillant"),
    INCONNU("Inconnu");

    private final String label;

    TankCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
