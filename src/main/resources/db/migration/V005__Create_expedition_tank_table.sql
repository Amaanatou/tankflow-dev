CREATE TABLE expedition_tank (
    id BIGSERIAL PRIMARY KEY,
    expedition_id BIGINT NOT NULL,
    cycle_id BIGINT NOT NULL,
    tank_id BIGINT NOT NULL,
    selected BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (expedition_id) REFERENCES expedition(id) ON DELETE CASCADE,
    FOREIGN KEY (cycle_id) REFERENCES tank_cycle(id) ON DELETE CASCADE,
    FOREIGN KEY (tank_id) REFERENCES tank_asset(id) ON DELETE CASCADE,
    UNIQUE(expedition_id, cycle_id)
);

CREATE INDEX idx_expedition_tank_expedition ON expedition_tank(expedition_id);
CREATE INDEX idx_expedition_tank_cycle ON expedition_tank(cycle_id);
CREATE INDEX idx_expedition_tank_tank ON expedition_tank(tank_id);
CREATE INDEX idx_expedition_tank_selected ON expedition_tank(selected);
