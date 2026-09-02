-- Renommer les tables aux noms stratégiques
ALTER TABLE tank_asset RENAME TO tank;
ALTER TABLE tank_cycle RENAME TO cycle;

-- Supprimer les tables non-stratégiques
DROP TABLE IF EXISTS alert CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;

-- Mettre à jour les FKs après renommage
ALTER TABLE cycle DROP CONSTRAINT IF EXISTS tank_cycle_asset_id_fkey;
ALTER TABLE cycle ADD CONSTRAINT cycle_tank_id_fkey FOREIGN KEY (asset_id) REFERENCES tank(id);

ALTER TABLE expedition_tank DROP CONSTRAINT IF EXISTS expedition_tank_tank_id_fkey;
ALTER TABLE expedition_tank ADD CONSTRAINT expedition_tank_tank_id_fkey FOREIGN KEY (tank_id) REFERENCES tank(id);

-- Renommer les index
ALTER INDEX idx_tank_asset_serial RENAME TO idx_tank_serial;
ALTER INDEX idx_tank_asset_supplier RENAME TO idx_tank_supplier;
ALTER INDEX idx_tank_cycle_asset RENAME TO idx_cycle_tank;
ALTER INDEX idx_tank_cycle_status RENAME TO idx_cycle_status;
ALTER INDEX idx_tank_cycle_deadline RENAME TO idx_cycle_deadline;
ALTER INDEX idx_tank_cycle_code RENAME TO idx_cycle_code;
