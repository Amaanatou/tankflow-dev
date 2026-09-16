package com.seneau.tankflow.data.enumeration;

public enum ExpeditionType {
    FOURNISSEUR_VERS_SITE_PRINCIPAL("Fournisseur vers site principal"),
    SITE_PRINCIPAL_VERS_FOURNISSEUR("Site principal vers fournisseur");

    private final String label;

    ExpeditionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
