package com.seneau.tankflow.data.enumeration;

public enum ExpeditionStatus {
    SENT("Expédiée"),
    EN_ROUTE("En transit"),
    RECEIVED("Reçue"),
    CANCELLED("Annulée");

    private final String label;

    ExpeditionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
