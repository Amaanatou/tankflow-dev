package com.seneau.tankflow.data.enumeration;

public enum TankCondition {
    COMPLIANT("Conforme"),
    NON_COMPLIANT("Non conforme"),
    UNKNOWN("Inconnu");

    private final String label;

    TankCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
