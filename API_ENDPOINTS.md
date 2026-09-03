# 📚 TankFlow API - Documentation des Endpoints

**Version:** 1.0  
**Date:** 2026-09-03  
**Base URL:** `http://localhost:9010/api/v1/tankflow`

---

## 🔐 Configuration de Base

- **Port:** 9010
- **Context Path:** `/api/v1/tankflow`
- **Format:** JSON
- **Content-Type:** `application/json`

---

## 1️⃣ TANK - Réservoirs Physiques

### Créer un tank
```
POST /api/v1/tanks
```

**Body (JSON):**
```json
{
  "manufacturerSerial": "TANK-2024-001",
  "supplierId": "SUPP-001",
  "hasSafetyBell": true,
  "tankStatus": "ACTIVE",
  "notes": "Tank en bon état"
}
```

**Réponse (201 Created):**
```json
{
  "id": 1,
  "manufacturerSerial": "TANK-2024-001",
  "supplierId": "SUPP-001",
  "hasSafetyBell": true,
  "tankStatus": "ACTIVE",
  "notes": "Tank en bon état",
  "createdAt": "2026-09-03T10:00:00",
  "updatedAt": "2026-09-03T10:00:00"
}
```

---

### Lister tous les tanks
```
GET /api/v1/tanks
```

**Réponse (200 OK):**
```json
[
  {
    "id": 1,
    "manufacturerSerial": "TANK-2024-001",
    "supplierId": "SUPP-001",
    "hasSafetyBell": true,
    "tankStatus": "ACTIVE",
    "notes": "Tank en bon état",
    "createdAt": "2026-09-03T10:00:00",
    "updatedAt": "2026-09-03T10:00:00"
  }
]
```

---

### Récupérer un tank par ID
```
GET /api/v1/tanks/{id}
```

**Paramètres:**
- `id` (path, required): ID du tank

**Réponse (200 OK):**
```json
{
  "id": 1,
  "manufacturerSerial": "TANK-2024-001",
  "supplierId": "SUPP-001",
  "hasSafetyBell": true,
  "tankStatus": "ACTIVE",
  "notes": "Tank en bon état",
  "createdAt": "2026-09-03T10:00:00",
  "updatedAt": "2026-09-03T10:00:00"
}
```

---

### Récupérer un tank par numéro de série
```
GET /api/v1/tanks/serial/{manufacturerSerial}
```

**Paramètres:**
- `manufacturerSerial` (path, required): Numéro de série du fabricant

**Réponse (200 OK):** Même format que ci-dessus

---

### Mettre à jour un tank
```
PUT /api/v1/tanks/{id}
```

**Paramètres:**
- `id` (path, required): ID du tank

**Body (JSON):**
```json
{
  "manufacturerSerial": "TANK-2024-001",
  "supplierId": "SUPP-002",
  "hasSafetyBell": false,
  "tankStatus": "MAINTENANCE",
  "notes": "Maintenance prévue"
}
```

**Réponse (200 OK):** Objet tank mis à jour

---

### Supprimer un tank
```
DELETE /api/v1/tanks/{id}
```

**Paramètres:**
- `id` (path, required): ID du tank

**Réponse (204 No Content)**

---

## 2️⃣ CYCLE - Cycles/Rotations des Tanks

### Créer un cycle
```
POST /api/v1/cycles
```

**Body (JSON):**
```json
{
  "assetId": 1,
  "startedAt": "2026-09-01T10:00:00",
  "deadlineAt": "2026-12-20T23:59:59"
}
```

**Réponse (201 Created):**
```json
{
  "id": 1,
  "publicCode": "TANK-2024-001-0001",
  "assetId": 1,
  "startedAt": "2026-09-01T10:00:00",
  "returnedToSupplierAt": null,
  "deadlineAt": "2026-12-20T23:59:59",
  "status": "IN_PROGRESS",
  "currentStepNumber": 1,
  "durationDays": 111,
  "daysRemaining": 108,
  "penaltyAmount": 0.00,
  "isPenaltyApplied": false,
  "version": 0,
  "createdAt": "2026-09-03T10:00:00",
  "updatedAt": "2026-09-03T10:00:00"
}
```

---

### Récupérer un cycle par ID
```
GET /api/v1/cycles/{id}
```

**Paramètres:**
- `id` (path, required): ID du cycle

**Réponse (200 OK):** Objet cycle

---

### Récupérer les cycles d'un tank
```
GET /api/v1/cycles/asset/{assetId}
```

**Paramètres:**
- `assetId` (path, required): ID du tank

**Réponse (200 OK):** Array de cycles

---

### Récupérer les cycles par statut
```
GET /api/v1/cycles/status/{status}
```

**Paramètres:**
- `status` (path, required): `IN_PROGRESS` ou `COMPLETED`

**Réponse (200 OK):** Array de cycles

---

### Mettre à jour le statut d'un cycle
```
PUT /api/v1/cycles/{id}/status?status={status}
```

**Paramètres:**
- `id` (path, required): ID du cycle
- `status` (query, required): `IN_PROGRESS` ou `COMPLETED`

**Réponse (200 OK):** Cycle mis à jour

---

### Enregistrer la date de retour
```
PUT /api/v1/cycles/{id}/return?returnedAt={datetime}
```

**Paramètres:**
- `id` (path, required): ID du cycle
- `returnedAt` (query, required): Format ISO 8601 (ex: `2026-12-15T14:30:00`)

**Réponse (200 OK):** Cycle mis à jour avec date de retour

---

## 3️⃣ WORKFLOW_EVENT - Événements du Workflow

### Enregistrer un événement
```
POST /api/v1/cycles/{cycleId}/events
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle

**Body (JSON):**
```json
{
  "step": "SHIPPED",
  "locationId": 1,
  "zoneId": 1,
  "performedByUserId": 1,
  "idempotencyKey": "device-001:event-12345"
}
```

**Réponse (201 Created):**
```json
{
  "id": 1,
  "cycleId": 1,
  "cycleStageId": null,
  "stepNumber": 2,
  "eventType": "SHIPPED",
  "locationId": 1,
  "zoneId": 1,
  "performedByUserId": 1,
  "eventTimestamp": "2026-09-03T10:30:00",
  "idempotencyKey": "device-001:event-12345",
  "metadata": null,
  "createdAt": "2026-09-03T10:30:00"
}
```

---

### Lister les événements d'un cycle
```
GET /api/v1/cycles/{cycleId}/events
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle

**Réponse (200 OK):** Array d'événements

---

### Récupérer un événement spécifique
```
GET /api/v1/cycles/{cycleId}/events/{eventId}
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle
- `eventId` (path, required): ID de l'événement

**Réponse (200 OK):** Objet événement

---

### Récupérer l'étape courante
```
GET /api/v1/cycles/{cycleId}/events/current-step
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle

**Réponse (200 OK):**
```json
"Expedition"
```

---

### Récupérer l'étape suivante
```
GET /api/v1/cycles/{cycleId}/events/next-step
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle

**Réponse (200 OK):**
```json
"Réception"
```

---

### Vérifier si transition possible
```
GET /api/v1/cycles/{cycleId}/events/can-transition/{stepNumber}
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle
- `stepNumber` (path, required): Numéro d'étape (1-9)

**Réponse (200 OK):**
```json
true
```

---

### Vérifier clé idempotence
```
GET /api/v1/cycles/{cycleId}/events/idempotency/{key}/processed
```

**Paramètres:**
- `cycleId` (path, required): ID du cycle
- `key` (path, required): Clé d'idempotence

**Réponse (200 OK):**
```json
true
```

---

## 4️⃣ EXPEDITION - Expéditions Logistiques

### Créer une expédition
```
POST /api/v1/expeditions
```

**Body (JSON):**
```json
{
  "reference": "EXP-2024-001",
  "type": "IMPORT",
  "origine": "Shanghai",
  "destination": "Dakar",
  "statut": "EN_COURS"
}
```

**Réponse (201 Created):**
```json
{
  "id": 1,
  "reference": "EXP-2024-001",
  "type": "IMPORT",
  "origine": "Shanghai",
  "destination": "Dakar",
  "statut": "EN_COURS",
  "dateDepart": "2026-09-03T10:00:00",
  "dateArrivee": null,
  "createdAt": "2026-09-03T10:00:00",
  "updatedAt": "2026-09-03T10:00:00"
}
```

---

### Récupérer une expédition
```
GET /api/v1/expeditions/{id}
```

**Paramètres:**
- `id` (path, required): ID de l'expédition

**Réponse (200 OK):** Objet expédition

---

### Récupérer par référence
```
GET /api/v1/expeditions/reference/{reference}
```

**Paramètres:**
- `reference` (path, required): Numéro de référence

**Réponse (200 OK):** Objet expédition

---

### Récupérer par statut
```
GET /api/v1/expeditions/statut/{statut}
```

**Paramètres:**
- `statut` (path, required): Statut de l'expédition

**Réponse (200 OK):** Array d'expéditions

---

### Récupérer par origine
```
GET /api/v1/expeditions/origine/{origine}
```

**Paramètres:**
- `origine` (path, required): Pays/lieu d'origine

**Réponse (200 OK):** Array d'expéditions

---

### Récupérer par destination
```
GET /api/v1/expeditions/destination/{destination}
```

**Paramètres:**
- `destination` (path, required): Pays/lieu de destination

**Réponse (200 OK):** Array d'expéditions

---

### Mettre à jour le statut
```
PUT /api/v1/expeditions/{id}/statut?statut={statut}
```

**Paramètres:**
- `id` (path, required): ID de l'expédition
- `statut` (query, required): Nouveau statut

**Réponse (200 OK):** Expédition mise à jour

---

### Marquer comme arrivée
```
PUT /api/v1/expeditions/{id}/arrival
```

**Paramètres:**
- `id` (path, required): ID de l'expédition

**Réponse (200 OK):** Expédition avec dateArrivee remplie

---

## 5️⃣ EXPEDITION_TANK - Liaison Cycles-Expéditions

### Ajouter un cycle à une expédition
```
POST /api/v1/expeditions/{expeditionId}/cycles
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition

**Body (JSON):**
```json
{
  "cycleId": 1,
  "tankId": 1
}
```

**Réponse (201 Created):**
```json
{
  "id": 1,
  "expeditionId": 1,
  "cycleId": 1,
  "tankId": 1,
  "selected": false,
  "createdAt": "2026-09-03T10:00:00"
}
```

---

### Lister les cycles d'une expédition
```
GET /api/v1/expeditions/{expeditionId}/cycles
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition

**Réponse (200 OK):** Array de liaisons

---

### Récupérer une liaison spécifique
```
GET /api/v1/expeditions/{expeditionId}/cycles/{id}
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition
- `id` (path, required): ID de la liaison

**Réponse (200 OK):** Objet liaison

---

### Marquer comme sélectionné
```
PUT /api/v1/expeditions/{expeditionId}/cycles/{id}/select
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition
- `id` (path, required): ID de la liaison

**Réponse (200 OK):** Liaison avec selected = true

---

### Retirer un cycle d'une expédition
```
DELETE /api/v1/expeditions/{expeditionId}/cycles/{id}
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition
- `id` (path, required): ID de la liaison

**Réponse (204 No Content)**

---

### Compter les cycles d'une expédition
```
GET /api/v1/expeditions/{expeditionId}/cycles/count
```

**Paramètres:**
- `expeditionId` (path, required): ID de l'expédition

**Réponse (200 OK):**
```json
5
```

---

## ⚠️ Codes d'Erreur

| Code | Signification |
|------|---------------|
| 200 | OK - Requête réussie |
| 201 | Created - Ressource créée |
| 204 | No Content - Suppression réussie |
| 400 | Bad Request - Paramètres invalides |
| 404 | Not Found - Ressource non trouvée |
| 500 | Internal Server Error - Erreur serveur |

---

## 🔧 Exemple avec cURL

```bash
# Créer un tank
curl -X POST http://localhost:9010/api/v1/tankflow/api/v1/tanks \
  -H "Content-Type: application/json" \
  -d '{
    "manufacturerSerial": "TANK-2024-001",
    "supplierId": "SUPP-001",
    "hasSafetyBell": true,
    "tankStatus": "ACTIVE",
    "notes": "Nouveau tank"
  }'

# Récupérer tous les tanks
curl http://localhost:9010/api/v1/tankflow/api/v1/tanks

# Créer un cycle
curl -X POST http://localhost:9010/api/v1/tankflow/api/v1/cycles \
  -H "Content-Type: application/json" \
  -d '{
    "assetId": 1,
    "startedAt": "2026-09-01T10:00:00",
    "deadlineAt": "2026-12-20T23:59:59"
  }'
```

---

**Dernière mise à jour:** 2026-09-03
