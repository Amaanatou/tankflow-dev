CREATE TABLE alert (
    id BIGSERIAL PRIMARY KEY,
    cycle_id BIGINT NOT NULL,
    level VARCHAR(50) NOT NULL,
    status VARCHAR(50),
    threshold_days INTEGER,
    triggered_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    resolved_by_user_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (cycle_id) REFERENCES tank_cycle(id)
);

CREATE INDEX idx_alert_cycle ON alert(cycle_id);
CREATE INDEX idx_alert_status ON alert(status);
CREATE INDEX idx_alert_level ON alert(level);
