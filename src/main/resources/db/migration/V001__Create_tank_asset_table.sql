CREATE TABLE tank_asset (
    id BIGSERIAL PRIMARY KEY,
    manufacturer_serial VARCHAR(100) UNIQUE NOT NULL,
    supplier_id VARCHAR(100) NOT NULL,
    has_safety_bell BOOLEAN DEFAULT TRUE NOT NULL,
    tank_status VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_tank_asset_serial ON tank_asset(manufacturer_serial);
CREATE INDEX idx_tank_asset_supplier ON tank_asset(supplier_id);
