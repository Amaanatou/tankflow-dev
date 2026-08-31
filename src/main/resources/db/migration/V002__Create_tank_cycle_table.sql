CREATE TABLE tank_cycle (
    id BIGSERIAL PRIMARY KEY,
    public_code VARCHAR(100) UNIQUE NOT NULL,
    asset_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    returned_to_supplier_at TIMESTAMP,
    deadline_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    current_step_number INTEGER NOT NULL,
    duration_days INTEGER,
    days_remaining INTEGER,
    penalty_amount DECIMAL(10, 2) DEFAULT 0.00,
    is_penalty_applied BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES tank_asset(id)
);

CREATE INDEX idx_tank_cycle_asset ON tank_cycle(asset_id);
CREATE INDEX idx_tank_cycle_status ON tank_cycle(status);
CREATE INDEX idx_tank_cycle_deadline ON tank_cycle(deadline_at);
CREATE INDEX idx_tank_cycle_code ON tank_cycle(public_code);
