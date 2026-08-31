package com.seneau.tankflow.data.enumeration;

public enum CycleStatus {
    IN_PROGRESS("En cours"),
    COMPLETED("Complété"),
    OVERDUE("En retard");

    private final String label;

    CycleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
