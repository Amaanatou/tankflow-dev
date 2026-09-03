# 📋 AUDIT RÉEL DU PROJET + ROADMAP PRIORISÉE

**Date:** 2026-09-03  
**Scope:** Basé sur CDC, Spec Technique, Guide Explicatif, Workflow & Profils  
**Décision:** S'arrêter ici jusqu'à mettre tout en place. Pas de nouvelles tables/rôles avant.

---

## 1️⃣ AUDIT RÉEL : Ce qui est FAIT vs Ce qui MANQUE

### ✅ FAIT (à 70%)

| Élément | État | Détail |
|---------|------|--------|
| **Architecture de base** | ✅ OK | Contrôleurs, Services, Repositories, Entités |
| **5 entités principales** | ✅ OK | TankAsset, TankCycle, WorkflowEvent, Expedition, ExpeditionTank |
| **5 contrôleurs REST** | ✅ OK | /api/v1/tanks, /api/v1/cycles, /api/v1/cycles/{id}/events, /api/v1/expeditions, /api/v1/expeditions/{id}/cycles |
| **Base PostgreSQL** | ✅ OK | 5 tables + flyway_schema_history |
| **Migrations Flyway** | ✅ OK | V001-V010 (renommage + création shipment) |
| **Documentation API** | ✅ OK | API_ENDPOINTS.md |
| **Git + Commits** | ✅ OK | Historique propre |

### ❌ MANQUE CRITICAL (avant de livrer au frontend)

#### **Pattern #1 : Idempotence** ❌ MANQUANT
```
Exigence CDC: Rejouable sans créer deux fois le même événement
Implémentation: Idempotency-Key header + UNIQUE constraint
Statut: ❌ À faire
Priorité: 🔴 HAUTE
```

#### **Pattern #2 : Optimistic Locking** ⚠️ PARTIEL
```
Exigence Spec: @Version sur TankCycle pour éviter les conflits
Implémentation: Entity a le champ version, mais pas testé
Statut: ⚠️ À tester + implémenter côté contrôleur (409 Conflict)
Priorité: 🔴 HAUTE
```

#### **Pattern #3 : Outbox Pattern** ❌ MANQUANT
```
Exigence Spec: Fiabilité des notifications async
Implémentation: Table outbox_event + Consumer async
Statut: ❌ Pas de table, pas de consumer
Priorité: 🟡 MOYENNE (peut attendre Phase 2)
```

#### **Pattern #4 : State Machine (Transitions)** ❌ MANQUANT
```
Exigence Spec: Valider transitions ÉTAPE 1→2→...→9
Implémentation: Règles métier, vérification profil/site
Statut: ❌ À faire
Priorité: 🔴 HAUTE (métier critique)
```

#### **Pattern #5 : Event Sourcing léger** ⚠️ PARTIEL
```
Exigence Spec: WorkflowEvent immuable (never update/delete)
Implémentation: Entity existe, mais pas d'enforcement
Statut: ⚠️ À sécuriser (pas de UPDATE/DELETE possibles)
Priorité: 🟡 MOYENNE
```

#### **Sécurité & Authentification** ❌ ABSENT
```
Exigence CDC: Spring Security + JWT/OIDC + RBAC
Implémentation: Aucune
Statut: ❌ À faire
Priorité: 🔴 HAUTE (non-négociable)
```

#### **AlertScheduler** ❌ ABSENT
```
Exigence CDC: Job toutes les 15 minutes pour alertes J-15, J-10, J-5, J+0
Implémentation: Aucune
Statut: ❌ À faire
Priorité: 🟡 MOYENNE (Phase 2)
```

#### **Validation des transitions** ❌ ABSENT
```
Exigence Spec: PUT /api/v1/cycles/{id}/transition doit vérifier :
  - Étape actuelle (1-9)
  - Profil utilisateur
  - Site/localisation
  - Pièces obligatoires (BL, bon de transfert, etc.)
Implémentation: Aucune
Statut: ❌ À faire
Priorité: 🔴 HAUTE (critique métier)
```

#### **Tests** ❌ ABSENT
```
Exigence Spec: JUnit 5, Mockito, Testcontainers PostgreSQL
Implémentation: 0 tests
Statut: ❌ À faire
Priorité: 🔴 HAUTE (qualité)
```

#### **Exception Handler Global** ❌ ABSENT
```
Implémentation: Aucune
Statut: ❌ À faire
Priorité: 🟡 MOYENNE
```

#### **Logging structuré** ⚠️ BASIQUE
```
Implémentation: Logs basiques avec @Slf4j
Statut: ⚠️ À améliorer
Priorité: 🟢 BASSE (peut attendre)
```

---

## 2️⃣ ROADMAP PRIORISÉE - 6 PHASES

### 🔴 PHASE 1 : SÉCURITÉ & FONDATIONS (Sem 1)
**Objectif:** Rendre l'app utilisable et sécurisée

- [ ] **Spring Security + JWT** (2j)
  - Configuration SecurityConfig
  - JWT token generation/validation
  - RBAC basique (5 rôles)
  - Endpoint /api/v1/auth/login

- [ ] **Exception Handler Global** (1j)
  - GlobalExceptionHandler
  - Codes d'erreur standardisés (409 Conflict, 422 Unprocessable, etc.)
  - Format de réponse d'erreur cohérent

- [ ] **Validation améliorée** (1j)
  - @NotBlank, @Positive, @Valid sur les DTOs
  - Messages d'erreur clairs
  - Validation métier (ex: date future pour deadline)

**Livrable:** Application sécurisée avec logins

---

### 🔴 PHASE 2 : PATTERNS CRITIQUES (Sem 2)
**Objectif:** Implémenter les patterns qui garantissent la fiabilité

- [ ] **Idempotence** (2j)
  - Ajouter Idempotency-Key header handler
  - Créer table `idempotency_log` (request_id, response)
  - Tester replay sans doublon

- [ ] **Optimistic Locking** (1j)
  - Implémenter la gestion des 409 Conflict
  - Retry côté client (si nécessaire)
  - Tests de concurrence

- [ ] **State Machine (Transitions)** (2j)
  - Créer `TransitionValidator`
  - Règles : étape actuelle → étape suivante
  - Vérification profil/site/pièces obligatoires
  - Endpoint: PUT /api/v1/cycles/{id}/transition

**Livrable:** Scans sécurisés et rejouables

---

### 🟡 PHASE 3 : TESTS (Sem 2-3)
**Objectif:** Qualité et couverture de code

- [ ] **Tests unitaires** (2j)
  - Services
  - Validators
  - DTOs

- [ ] **Tests d'intégration** (2j)
  - Testcontainers PostgreSQL
  - Workflows complets (création cycle → scans → clôture)
  - Transitions invalides

- [ ] **Tests API** (1j)
  - MockMvc pour les contrôleurs
  - Cas normaux et cas d'erreur

**Target:** >70% coverage

**Livrable:** Suite de tests complète

---

### 🟡 PHASE 4 : ALERTES & DASHBOARD (Sem 3)
**Objectif:** Fonctionnalités métier

- [ ] **AlertScheduler** (1j)
  - Job @Scheduled toutes les 15 min
  - Calcul J-15, J-10, J-5, J+0
  - Création Alert entities

- [ ] **Dashboard endpoints** (1j)
  - GET /api/v1/dashboard/summary
  - Tanks par statut, alertes, pénalités
  - Stats par étape/usine

- [ ] **Export & Rapports** (1j)
  - GET /api/v1/reports/cycles avec filtres
  - Export Excel (optionnel Phase 2)

**Livrable:** KPI en temps réel

---

### 🟢 PHASE 5 : OUTBOX & ASYNC (Sem 4)
**Objectif:** Fiabilité des notifications

- [ ] **Outbox Pattern** (2j)
  - Table outbox_event
  - À chaque transaction : WorkflowEvent + OutboxEvent
  - Consumer async (polling 10sec)
  - Retry logic

- [ ] **Notifications** (1j)
  - Email ou push (stub pour Phase 2)
  - Integration avec OutboxEvent

**Livrable:** Système de notifications fiable

---

### 🟢 PHASE 6 : DOCUMENTATION & DEPLOYMENT (Sem 4-5)
**Objectif:** Prêt pour production

- [ ] **Swagger/OpenAPI** (1j)
  - Annotations @Operation, @ApiResponse
  - Documentation interactive

- [ ] **Docker** (1j)
  - Dockerfile pour l'app
  - docker-compose avec PostgreSQL

- [ ] **Configuration prod** (0.5j)
  - application-dev.yml, application-prod.yml
  - Env vars pour BD, JWT secret, etc.

- [ ] **Documentation utilisateur** (1j)
  - Guide déploiement
  - Configuration
  - Troubleshooting

**Livrable:** App déployable

---

## 3️⃣ TIMELINE GLOBALE

```
PHASE 1 (Sécurité)       : 4 jours       [████      ] Sem 1
PHASE 2 (Patterns)       : 5 jours       [████████  ] Sem 1-2
PHASE 3 (Tests)          : 5 jours       [████████  ] Sem 2-3
PHASE 4 (Alertes/Dash)   : 3 jours       [██████    ] Sem 3
PHASE 5 (Outbox)         : 3 jours       [██████    ] Sem 4
PHASE 6 (Docs/Deploy)    : 3 jours       [██████    ] Sem 4-5
───────────────────────────────────────────────────────────
TOTAL                    : ~4-5 semaines

Livraison au Frontend: Fin de Sem 3 (avec Phases 1-3 ✅)
```

---

## 4️⃣ TABLEAU COMPARATIF : CDC vs Actuellement

| Exigence CDC | Implémentation actuelle | État | Correction Phase |
|--------------|-------------------------|------|-----------------|
| 9 étapes workflow | Entités créées | ⚠️ Partiel | Phase 2 (State Machine) |
| Scan QR/code-barres | Endpoint existe | ✅ OK | (sera testé Phase 3) |
| Délai 180j, pénalité | Pas d'alertes | ❌ Non | Phase 4 (Scheduler) |
| Alertes J-15/10/5/+0 | Aucun système | ❌ Non | Phase 4 (Scheduler) |
| 5 profils + droits | Pas de Spring Security | ❌ Non | Phase 1 (RBAC) |
| Idempotence | Aucune | ❌ Non | Phase 2 |
| Traçabilité complète | Basique (inserts seulement) | ⚠️ Partiel | Phase 5 (Outbox) |
| Disponibilité 99.5% | À valider | ❓ ? | Phase 5+ (monitoring) |
| Scan offline possible | Non supporté | ❌ Non | Phase 5 (sync batch) |

---

## 5️⃣ CE QUE J'ARRÊTE POUR LE MOMENT

❌ **Ne pas créer de nouvelles tables**
- `location`, `supplier`, `outbox_event` (peuvent attendre Phase 5)
- `user`, `role` (Spring Security suffira)

❌ **Ne pas créer 6 rôles**
- Implémenter les 5 profils CDC en RBAC Spring
- Étendre à 6 après Phase 1

❌ **Ne pas faire Shipment → Expedition**
- Les tables existent (expedition, expedition_tank)
- Les services aussi
- On testera juste sans élargir

---

## 6️⃣ COMMANDES GIT POUR DÉMARRER

```bash
# Phase 1: Branch sécurité
git checkout -b feature/phase-1-security

# Phase 2: Branch patterns
git checkout -b feature/phase-2-patterns

# etc.
```

---

## 7️⃣ QUESTIONS AVANT DE COMMENCER

### Pour VOUS
1. **Délai du projet?** (2 sem, 1 mois, flexible?)
2. **Équipe disponible?** (1 dev, 2 devs?)
3. **Frontend prêt à tester après Phase 3?**
4. **Priorité: Sécurité d'abord ou Fonctionnalités d'abord?**
5. **Tests: couverture min (70%)? obligatoire dès Phase 3?**

### Pour LE PROJET
1. Dépendances Spring à ajouter (Spring Security, etc.)?
2. Stratégie JWT: Simple JWT ou OAuth2?
3. Email/SMS pour alertes ou juste dashboard?
4. Stockage fichiers (export Excel) local ou S3?

---

## 📌 RÉSUMÉ

- ✅ **Fondations solides** : architecture OK, entités OK, API OK
- ❌ **Patterns critiques manquants** : Idempotence, State Machine, Sécurité
- 🔄 **4-5 semaines de travail** pour un MVP prêt production
- 🚫 **STOP nouvelles tables/rôles** jusqu'à Phase 1 ✅

**Prêt à démarrer Phase 1 ?** 🚀

---

*Audit basé sur:*
- *Cahier des Charges (CDC)*
- *Specification Technique Spring Boot*
- *Guide Explicatif Traçabilité*
- *Workflow & Profils Opérationnel*
- *Inspection du code actuellement créé*
