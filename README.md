# 🚀 TankFlow - Backend Service

Gestion complète des cycles et expéditions de tanks avec API REST Spring Boot.

## 📋 À Propos

Backend TankFlow pour le système de gestion des réservoirs logistiques SEN'EAU. Implémente 3 features majeures :

1. **Gestion des Tanks par Numéro** - Ajout en batch avec validation
2. **Date de Réception Flexible** - Support dates antérieures (rétroactif)
3. **Durée par Étape** - Calcul automatique du temps passé à chaque étape

## 🛠️ Stack Technique

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 3.3.0
- **Base de Données**: PostgreSQL 14+
- **Build**: Maven
- **ORM**: Hibernate/JPA
- **Migrations BD**: Flyway
- **Authentification**: JWT
- **Tests**: JUnit 5, Mockito

## 📦 Dépendances Clés

```xml
<!-- Spring Boot -->
<dependency>spring-boot-starter-web</dependency>
<dependency>spring-boot-starter-data-jpa</dependency>
<dependency>spring-boot-starter-security</dependency>

<!-- Database -->
<dependency>org.postgresql:postgresql</dependency>
<dependency>org.flywaydb:flyway-core</dependency>

<!-- JWT -->
<dependency>io.jsonwebtoken:jjwt-api:0.12.3</dependency>

<!-- Lombok -->
<dependency>org.projectlombok:lombok</dependency>

<!-- Testing -->
<dependency>org.springframework.boot:spring-boot-starter-test</dependency>
```

## 🚀 Quick Start

### 1. Cloner le Repository
```bash
git clone https://github.com/Amaanatou/tankflow-dev.git
cd tankflow-dev
git checkout new-feat  # Branch avec les nouvelles features
```

### 2. Configuration Base de Données

Créer une base PostgreSQL:
```sql
CREATE DATABASE tankflow;
```

Configurer `.env`:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tankflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=votre_password
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

### 3. Compiler & Démarrer

```bash
# Compiler
mvn clean compile

# Tests
mvn test

# Run
mvn spring-boot:run
```

L'API est disponible à `http://localhost:8080`

## 📚 API Endpoints

### 1. Ajouter Tanks par Numéro

```
POST /api/v1/expeditions/{expeditionId}/cycles/by-serial

Request:
{
  "manufacturerSerials": ["TANK-001", "TANK-002", "TANK-003"]
}

Response:
{
  "added": [...],
  "unavailable": [...],
  "notFound": [...],
  "duplicates": [...],
  "successCount": 2,
  "errorCount": 1,
  "allSuccessful": false
}
```

### 2. Marquer Expédition comme Reçue

```
PUT /api/v1/expeditions/{id}/arrival

Request:
{
  "dateArrivee": "2024-09-15T14:30:00"  // Date flexible
}

Response:
{
  "id": 1,
  "reference": "EXP-001",
  "statut": "LIVRÉE",
  "dateArrivee": "2024-09-15T14:30:00",
  ...
}
```

### 3. Consulter Durées par Étape

```
GET /api/v1/cycles/{id}/step-durations

Response:
[
  {
    "stepNumber": 1,
    "stepLabel": "Expédition fournisseur",
    "startedAt": "2024-01-01T10:00:00",
    "completedAt": "2024-01-05T14:30:00",
    "durationDays": 4.19,
    "durationDaysRounded": 4,
    "status": "COMPLETED"
  },
  ...
]
```

**Voir `IMPLEMENTATION_SUMMARY.md` pour tous les endpoints et détails**

## 🧪 Tests

```bash
# Tous les tests
mvn test

# Test spécifique
mvn test -Dtest=ExpeditionServiceTest

# Avec couverture
mvn test jacoco:report
```

**Status**: ✅ 14/14 tests passing

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/com/seneau/tankflow/
│   │   ├── config/           # Configuration (JWT, Dotenv)
│   │   ├── data/
│   │   │   ├── model/        # Entités JPA
│   │   │   ├── repository/   # Interfaces Repository
│   │   │   └── enumeration/  # Enums
│   │   ├── service/
│   │   │   ├── interfaces/   # Service interfaces
│   │   │   └── implementation/
│   │   └── web/
│   │       ├── controller/   # REST Controllers
│   │       ├── dto/          # Request/Response DTOs
│   │       └── exception/    # Exception handlers
│   └── resources/
│       ├── db/migration/     # Flyway migrations
│       ├── application.yml   # Config
│       └── application-dev.yml
└── test/
    └── java/com/seneau/tankflow/
        └── service/          # Unit tests
```

## 📊 Architecture Base de Données

5 tables stratégiques:
- **tank**: Identité permanente du tank
- **cycle**: Aller-retour d'un tank (210 jours)
- **workflow_event**: Événements immuables (9 étapes)
- **expedition**: Regroupement logistique
- **expedition_tank**: Association cycle↔expédition

**Migrations**: ✅ V001-V011 (Flyway)

## 🔐 Sécurité

- ✅ Spring Security intégré
- ✅ JWT Bearer tokens
- ✅ Validation input (@Valid)
- ✅ Exception handling centralisé
- ✅ Logs sécurisés (pas de données sensibles)

## 🚨 Troubleshooting

### Erreur PostgreSQL
```
FATAL: password authentication failed
```
→ Vérifier `.env` et credentials PostgreSQL

### Port déjà utilisé
```
Address already in use :8080
```
→ `lsof -i :8080` et tuer le processus

### Build failure
```
Cannot find symbol
```
→ `mvn clean compile` (reload Maven)

## 👥 Collaboration

**Backend**: Manetou Drame (@dmanetou)  
**Frontend**: Amaanatou  

Branches:
- `main`: Version stable
- `new-feat`: Features en développement (backend implémenté ✅)

## 📝 Prochaines Étapes

- [ ] Frontend: Adapter UI pour les 3 features
- [ ] Tests d'intégration avec PostgreSQL réelle
- [ ] Swagger/OpenAPI documentation
- [ ] Déploiement staging

## 📄 License

Privé - SEN'EAU

---

**Questions?** Consultez `IMPLEMENTATION_SUMMARY.md` ou ouvrez une issue.
