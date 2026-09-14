-- Sample data for development/testing
-- This file is optional - Flyway migrations are the source of truth for schema

-- Insert sample tanks
INSERT INTO tank (manufacturer_serial, supplier_id, has_safety_bell, tank_status, notes, created_at, updated_at)
VALUES
  ('TANK-001', 'SUP-A', true, 'ACTIVE', 'Tank en bon état', NOW(), NOW()),
  ('TANK-002', 'SUP-B', true, 'ACTIVE', 'Contrôle technique OK', NOW(), NOW()),
  ('TANK-003', 'SUP-A', false, 'ACTIVE', 'Pas de cloche de sécurité', NOW(), NOW()),
  ('TANK-004', 'SUP-C', true, 'MAINTENANCE', 'En révision', NOW(), NOW()),
  ('TANK-005', 'SUP-B', true, 'ACTIVE', NULL, NOW(), NOW())
ON CONFLICT (manufacturer_serial) DO NOTHING;

-- Insert sample cycles
INSERT INTO cycle (public_code, asset_id, started_at, deadline_at, status, current_step_number,
                   is_penalty_applied, version, created_at, updated_at)
VALUES
  ('TANK-001-2024-ABC001', 1, NOW() - INTERVAL '30 days', NOW() + INTERVAL '180 days', 'IN_PROGRESS', 3, false, 0, NOW(), NOW()),
  ('TANK-002-2024-ABC002', 2, NOW() - INTERVAL '10 days', NOW() + INTERVAL '200 days', 'IN_PROGRESS', 2, false, 0, NOW(), NOW()),
  ('TANK-003-2024-ABC003', 3, NOW() - INTERVAL '200 days', NOW() - INTERVAL '10 days', 'COMPLETED', 9, false, 0, NOW(), NOW())
ON CONFLICT (public_code) DO NOTHING;

-- Insert sample expeditions
INSERT INTO expedition (reference, type, origine, destination, statut, date_depart, date_arrivee, created_at, updated_at)
VALUES
  ('EXP-2024-001', 'OUTBOUND', 'Paris', 'Lyon', 'EN_ROUTE', NOW() - INTERVAL '5 days', NULL, NOW(), NOW()),
  ('EXP-2024-002', 'OUTBOUND', 'Marseille', 'Bordeaux', 'SENT', NOW() - INTERVAL '2 days', NULL, NOW(), NOW()),
  ('EXP-2024-003', 'INBOUND', 'Lyon', 'Paris', 'LIVRÉE', NOW() - INTERVAL '30 days', NOW() - INTERVAL '20 days', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Insert sample expedition_tank associations
INSERT INTO expedition_tank (expedition_id, cycle_id, tank_id, selected, created_at)
VALUES
  (1, 1, 1, true, NOW()),
  (1, 2, 2, false, NOW()),
  (2, 3, 3, true, NOW())
ON CONFLICT (expedition_id, cycle_id) DO NOTHING;

-- Insert sample workflow events
INSERT INTO workflow_event (cycle_id, step_number, event_type, performed_by_user_id, event_timestamp, idempotency_key, metadata, created_at, updated_at)
VALUES
  (1, 1, 'SHIPMENT_SENT', NULL, NOW() - INTERVAL '30 days', 'idempotency-001', '{"location": "Paris"}', NOW(), NOW()),
  (1, 2, 'MAIN_RECEPTION', NULL, NOW() - INTERVAL '25 days', 'idempotency-002', '{"location": "Lyon"}', NOW(), NOW()),
  (1, 3, 'ROUTED_TO_FACTORY', NULL, NOW() - INTERVAL '20 days', 'idempotency-003', NULL, NOW(), NOW()),
  (2, 1, 'SHIPMENT_SENT', NULL, NOW() - INTERVAL '10 days', 'idempotency-004', '{"location": "Marseille"}', NOW(), NOW()),
  (2, 2, 'MAIN_RECEPTION', NULL, NOW() - INTERVAL '8 days', 'idempotency-005', '{"location": "Bordeaux"}', NOW(), NOW()),
  (3, 1, 'SHIPMENT_SENT', NULL, NOW() - INTERVAL '200 days', 'idempotency-006', NULL, NOW(), NOW()),
  (3, 2, 'MAIN_RECEPTION', NULL, NOW() - INTERVAL '195 days', 'idempotency-007', NULL, NOW(), NOW()),
  (3, 3, 'ROUTED_TO_FACTORY', NULL, NOW() - INTERVAL '190 days', 'idempotency-008', NULL, NOW(), NOW()),
  (3, 4, 'FACTORY_RECEPTION', NULL, NOW() - INTERVAL '185 days', 'idempotency-009', NULL, NOW(), NOW()),
  (3, 5, 'START_USAGE', NULL, NOW() - INTERVAL '180 days', 'idempotency-010', NULL, NOW(), NOW()),
  (3, 6, 'END_USAGE', NULL, NOW() - INTERVAL '50 days', 'idempotency-011', NULL, NOW(), NOW()),
  (3, 7, 'RETURN_TO_MAIN', NULL, NOW() - INTERVAL '45 days', 'idempotency-012', NULL, NOW(), NOW()),
  (3, 8, 'MAIN_RETURN_RECEPTION', NULL, NOW() - INTERVAL '40 days', 'idempotency-013', NULL, NOW(), NOW()),
  (3, 9, 'RETURN_TO_SUPPLIER', NULL, NOW() - INTERVAL '10 days', 'idempotency-014', NULL, NOW(), NOW())
ON CONFLICT (idempotency_key) DO NOTHING;

-- Insert sample users (optional - if authentication is needed)
INSERT INTO "user" (email, password_hash, role, is_active, created_at, updated_at)
VALUES
  ('admin@seneau.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/D1e', 'ADMIN', true, NOW(), NOW()),
  ('user@seneau.fr', '$2a$10$V4ZjcPmR9kJjYwXvJYJRb.6RaDO.IfJnrKUBh6XLpRNj0qvG0RYBm', 'USER', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
