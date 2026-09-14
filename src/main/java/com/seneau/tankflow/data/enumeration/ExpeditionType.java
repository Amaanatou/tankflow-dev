package com.seneau.tankflow.data.enumeration;

public enum ExpeditionType {
    OUTBOUND("Expédition sortante"),
    INBOUND("Expédition entrante"),
    RETURN("Retour fournisseur");

    private final String label;

    ExpeditionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
