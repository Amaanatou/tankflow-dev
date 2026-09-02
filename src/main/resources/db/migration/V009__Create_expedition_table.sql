-- Create expedition table (strategic model)
CREATE TABLE IF NOT EXISTS expedition (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(100) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    origine VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    date_arrivee TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_expedition_reference ON expedition(reference);
CREATE INDEX IF NOT EXISTS idx_expedition_statut ON expedition(statut);
CREATE INDEX IF NOT EXISTS idx_expedition_origine ON expedition(origine);
CREATE INDEX IF NOT EXISTS idx_expedition_destination ON expedition(destination);
CREATE INDEX IF NOT EXISTS idx_expedition_date_depart ON expedition(date_depart);
