# 🔄 Alignment Backend ↔ Frontend - Changelog

**Date**: 2026-09-14  
**Alignement avec**: Frontend Ousmane Gueye  
**Status**: ⚠️ EN COURS - Compilation à corriger

---

## 📋 Changements Effectués

### ✅ Enums Créés
- `ExpeditionType`: OUTBOUND, INBOUND, RETURN
- `ExpeditionStatus`: SENT, EN_ROUTE, RECEIVED, CANCELLED
- `TankCondition`: BON, DEGRADÉ, DÉFAILLANT, INCONNU
- `CyclePosition`: EN_TRANSIT, STOCKE, EN_UTILISATION, VIDE, HORS_CYCLE

### ✅ Entités Modifiées

#### **Expedition**
- ✅ Ajoute `transporteur` (String 255)
- ✅ `type` convertie en `ExpeditionType` enum
- ✅ `statut` convertie en `ExpeditionStatus` enum

#### **TankCycle**
- ✅ Ajoute `position` (CyclePosition enum)
- ✅ Ajoute `localisation` (String 255)
- ✅ Ajoute `zone` (String 100)

#### **WorkflowEvent**
- ✅ Remplace `locationId` Long par `site` String
- ✅ Remplace `zoneId` Long par `zone` String
- ✅ Ajoute `tankCondition` (TankCondition enum)
- ✅ Ajoute `safetyBellPresent` (Boolean)
- ✅ Ajoute `documentReference` (String 255)
- ✅ Ajoute `comment` (TEXT)

### ✅ Migration Flyway
- ✅ `V012__Align_schema_with_frontend_model.sql` créée
- ✅ Ajoute colonnes manquantes sans breaking changes

---

## ⚠️ À Corriger - Erreurs de Compilation

### 🔴 WorkflowServiceImpl (Corrigé)
- ❌ `setLocationId(Long)` → ✅ `setSite(String)`
- ❌ `setZoneId(Long)` → ✅ `setZone(String)`

### 🔴 ExpeditionController (EN COURS)
- ❌ `expedition.getType()` retourne enum, ExpeditionResponse attend String
- ❌ `expedition.getStatut()` retourne enum, ExpeditionResponse attend String

**À corriger**:
1. Mettre à jour `ExpeditionResponse` DTO pour accepter enums
2. Mettre à jour `CreateExpeditionRequest` pour accepter enums
3. Mettre à jour `ExpeditionService` methods pour accepter enums

### 🔴 WorkflowEventController (EN COURS)
- ❌ `getLocationId()` n'existe plus (c'était locationId Long)
- ❌ `getZoneId()` n'existe plus (c'était zoneId Long)

**À corriger**:
1. Utiliser `getSite()` et `getZone()` String à la place

---

## 🎯 Prochaines Étapes (Avant Compilation)

### 1️⃣ DTOs à Mettre à Jour

**CreateExpeditionRequest.java**:
```java
@Enumerated(EnumType.STRING)
private ExpeditionType type;

@Enumerated(EnumType.STRING)
private ExpeditionStatus statut;

private String transporteur;  // NOUVEAU
```

**ExpeditionResponse.java**:
- Idem

**RecordWorkflowEventRequest.java** (si elle utilise locationId/zoneId):
- Remplacer par `site` et `zone` String

### 2️⃣ Controllers à Corriger

**ExpeditionController.java**:
```java
private ExpeditionResponse toResponse(Expedition expedition) {
    return new ExpeditionResponse(
            expedition.getId(),
            expedition.getReference(),
            expedition.getType().name(),  // Enum → String
            expedition.getOrigine(),
            expedition.getDestination(),
            expedition.getTransporteur(),  // NOUVEAU
            expedition.getStatut().name(),  // Enum → String
            expedition.getDateDepart(),
            expedition.getDateArrivee(),
            expedition.getCreatedAt(),
            expedition.getUpdatedAt()
    );
}
```

**WorkflowEventController.java**:
```java
private WorkflowEventResponse toResponse(WorkflowEvent event) {
    return new WorkflowEventResponse(
            event.getId(),
            event.getCycleId(),
            event.getStepNumber(),
            event.getEventType(),
            event.getSite(),      // ← Remplacer locationId
            event.getZone(),      // ← Remplacer zoneId
            event.getPerformedByUserId(),
            event.getEventTimestamp(),
            event.getTankCondition(),      // NOUVEAU
            event.getSafetyBellPresent(),  // NOUVEAU
            event.getDocumentReference(),  // NOUVEAU
            event.getComment()             // NOUVEAU
    );
}
```

### 3️⃣ Service Methods à Adapter

**ExpeditionService interface & impl**:
- Changer signatures pour accepter `ExpeditionType` et `ExpeditionStatus` enums

### 4️⃣ Tests à Mettre à Jour

- ExpeditionServiceTest: Utiliser enums
- Controllers tests: Mettre à jour les assertions

---

## 📊 Recapitulatif Alignment

| Entité | Front vs Back | Status |
|--------|---|---|
| Tank | numeroFabricant ↔ manufacturerSerial | ✅ Mappé |
| Tank | fournisseur ↔ supplierId | ⚠️ À clarifier (est-ce une String ou FK?) |
| Tank | Statut (5 valeurs) ↔ tankStatus (3 valeurs) | 🔴 **DEUX CONCEPTS** |
| Cycle | Statut métier ↔ CycleStatus | ✅ Ajout CyclePosition |
| Cycle | Localisation/Zone | ✅ Ajoutées |
| Expedition | transporteur | ✅ Ajouté |
| Expedition | type enum | ✅ Créé ExpeditionType |
| Expedition | statut enum | ✅ Créé ExpeditionStatus |
| WorkflowEvent | site/zone (String) | ✅ Remplacé locationId/zoneId |
| WorkflowEvent | tankCondition, etc. | ✅ Ajoutés |

---

## 🔑 Points Ouverts (À Discuter avec Collègue)

1. **TankStatus métier** : Deux domaines métier différents
   - Front: Position du tank dans le cycle (EN_TRANSIT, STOCKE, etc.)
   - Back: Utilisabilité du tank (ACTIVE, INACTIVE, MAINTENANCE)
   - **Solution**: Garder les deux ?

2. **supplierId** : Est-ce vraiment une String ID ou faut-il une entité Supplier ?
   - Impact: Pourrait requérir une FK

3. **Localisation/Zone** : Synchronisation avec WorkflowEvent ?
   - Dénormaliser sur TankCycle ET mettre à jour à chaque transition d'étape ?

---

## ✨ Après Compilation OK

1. Tests: Adapter tous les tests aux nouveaux enums
2. Swagger: Documenter les nouveaux champs
3. Migration de données: S'il y a des données existantes, comment les migrer ?
4. Frontend: Adapter les appels API pour enums (pas de String brutes)

---

**Personne responsable**: Manetou Dramé (@dmanetou)  
**Point de contact frontend**: Ousmane Gueye (ousmane.gueye@...)
