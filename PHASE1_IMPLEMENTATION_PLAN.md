# 🚀 PHASE 1 - PLAN D'IMPLÉMENTATION DÉTAILLÉ

## À faire : 5 fichiers critiques

### 1️⃣ UserRole Enum
```
Path: src/main/java/com/seneau/tankflow/data/enumeration/UserRole.java

Contenu:
- ADMIN
- COORDINATOR
- MAGASINIER_CENTRAL
- MAGASINIER_USINE
- AGENT_PRODUCTION
- RESPONSABLE_ACHATS
```

### 2️⃣ JwtTokenProvider
```
Path: src/main/java/com/seneau/tankflow/security/JwtTokenProvider.java

Responsabilité:
- generateToken(username, role) → JWT token
- validateToken(token) → boolean
- getUsernameFromToken(token) → String
- getRoleFromToken(token) → UserRole
- getExpirationFromToken(token) → Date
```

### 3️⃣ SecurityConfig
```
Path: src/main/java/com/seneau/tankflow/security/SecurityConfig.java

Responsabilité:
- Configuration Spring Security
- JWT Filter
- Password Encoder (BCrypt)
- CORS configuration
- Endpoints publics: /api/v1/auth/login
```

### 4️⃣ AuthController
```
Path: src/main/java/com/seneau/tankflow/web/controller/AuthController.java

Endpoints:
- POST /api/v1/auth/login
  Input: { username, password }
  Output: { token, expiresIn, role }
  
- POST /api/v1/auth/logout
```

### 5️⃣ IdempotencyKeyInterceptor
```
Path: src/main/java/com/seneau/tankflow/security/idempotency/IdempotencyKeyInterceptor.java

Responsabilité:
- Intercepter les requests avec header Idempotency-Key
- Vérifier si déjà traité (via cache/DB)
- Si oui: retourner la réponse en cache
- Si non: laisser passer et cacher la réponse
```

### 6️⃣ IdempotencyLog Entity
```
Path: src/main/java/com/seneau/tankflow/data/model/IdempotencyLog.java

Champs:
- id: Long
- idempotencyKey: String (UNIQUE)
- requestBody: String (JSON)
- responseBody: String (JSON)
- statusCode: int
- createdAt: LocalDateTime
- expiresAt: LocalDateTime (après 24h)
```

### 7️⃣ TransitionValidator + StateMachine
```
Path: src/main/java/com/seneau/tankflow/domain/cycle/TransitionValidator.java

Responsabilité:
- Valider transitions ÉTAPE 1→2→...→9
- Vérifier profil utilisateur
- Vérifier site/localisation
- Vérifier pièces obligatoires
- Lancer exception si transition interdite
```

---

## ORDRE D'IMPLÉMENTATION

```
1. UserRole enum (5 min)
   ↓
2. JwtTokenProvider (20 min) - TEST LOCAL
   ↓
3. SecurityConfig (15 min) - COMPILE
   ↓
4. AuthController (10 min) - TEST POSTMAN
   ↓
5. IdempotencyLog Entity (5 min) - MIGRATION FLYWAY
   ↓
6. IdempotencyKeyInterceptor (15 min) - ENREGISTRER DANS CONFIG
   ↓
7. TransitionValidator (30 min) - TESTER
   ↓
8. Tests JUnit (2h) - Toutes les classes
```

**Total: ~2 jours de dev**

---

## MIGRATIONS FLYWAY À CRÉER

### V011__Create_users_table.sql
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
```

### V012__Create_idempotency_log_table.sql
```sql
CREATE TABLE idempotency_log (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    request_body TEXT,
    response_body TEXT,
    status_code INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    CONSTRAINT fk_expires CHECK (expires_at > created_at)
);

CREATE INDEX idx_idempotency_key ON idempotency_log(idempotency_key);
CREATE INDEX idx_expires_at ON idempotency_log(expires_at);
```

---

## CONFIGURATION APPLICATION.YML À AJOUTER

```yaml
security:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-change-in-prod-min-32-chars}
    expiration: ${JWT_EXPIRATION:86400000}  # 24h en ms
    
  cors:
    allowed-origins: http://localhost:3000,http://localhost:4200
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    max-age: 3600

idempotency:
  enabled: true
  ttl-minutes: 1440  # 24h
  cache-size: 1000
```

---

## TESTS À ÉCRIRE

### JwtTokenProviderTest
- generateToken returns valid JWT
- validateToken returns true for valid token
- validateToken returns false for invalid token
- getRoleFromToken extracts role correctly
- Expiration works correctly

### SecurityConfigTest
- CORS headers present
- /auth/login is public
- Protected endpoints require JWT

### AuthControllerTest
- POST /auth/login with valid credentials
- POST /auth/login with invalid credentials (401)
- JWT token can access protected endpoint

### IdempotencyKeyInterceptorTest
- First request: cache response
- Second request with same key: return cached
- Third endpoint: different idempotency key = new request

### TransitionValidatorTest
- Valid transition: SHIPMENT → RECEIPT_CENTRAL ✅
- Invalid transition: SHIPMENT → IN_USE ❌
- Invalid profil: AGENT_PRODUCTION cannot do SHIPMENT step ❌
- Invalid site: USINE magasinier cannot do CENTRAL step ❌

---

## CHECKLIST D'IMPLÉMENTATION

- [ ] UserRole enum créé et compilé
- [ ] JwtTokenProvider fait et testé localement
- [ ] SecurityConfig fait, BCrypt activé
- [ ] AuthController fait, /auth/login opérationnel
- [ ] Migration V011 (users table) appliquée
- [ ] Migration V012 (idempotency_log table) appliquée
- [ ] IdempotencyLog entity créée
- [ ] IdempotencyKeyInterceptor fait et enregistré
- [ ] TransitionValidator fait
- [ ] Tests écrits et passent
- [ ] Compilation réussie (mvn clean compile)
- [ ] Postman test: login → token → appel endpoint protégé ✅

---

## COMMANDES GIT POUR CHAQUE ÉTAPE

```bash
# Étape 1
git add pom.xml
git commit -m "Add JWT dependency (jjwt)"

# Étape 2
git add src/main/java/com/seneau/tankflow/data/model/User.java
git add src/main/java/com/seneau/tankflow/data/enumeration/UserRole.java
git commit -m "Add User entity and UserRole enum"

# Étape 3
git add src/main/java/com/seneau/tankflow/security/JwtTokenProvider.java
git commit -m "Add JWT token provider"

# Étape 4
git add src/main/java/com/seneau/tankflow/security/SecurityConfig.java
git commit -m "Add Spring Security configuration with JWT filter"

# Étape 5
git add src/main/java/com/seneau/tankflow/web/controller/AuthController.java
git commit -m "Add authentication endpoints"

# Étape 6
git add src/main/resources/db/migration/V011__Create_users_table.sql
git add src/main/resources/db/migration/V012__Create_idempotency_log_table.sql
git commit -m "Add users and idempotency_log tables"

# Étape 7
git add src/main/java/com/seneau/tankflow/data/model/IdempotencyLog.java
git add src/main/java/com/seneau/tankflow/security/idempotency/IdempotencyKeyInterceptor.java
git commit -m "Add idempotency logging and caching"

# Étape 8
git add src/main/java/com/seneau/tankflow/domain/cycle/TransitionValidator.java
git commit -m "Add state machine and transition validator"

# Étape 9
git add src/test/java/com/seneau/tankflow/...
git commit -m "Add comprehensive tests for security, idempotence, and state machine"

# Configuration
git add src/main/resources/application.yml
git commit -m "Add JWT and idempotency configuration"
```

---

## PROCHAIN APPEL

Une fois les 5 fichiers créés, vous aurez:
✅ Spring Security + JWT
✅ Idempotence
✅ State Machine
✅ Optimistic Locking (déjà partiellement là)
✅ Tests

**Êtes-vous prêt à commencer l'implémentation?** ✅
