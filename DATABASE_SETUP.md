# 🗄️ Configuration Base de Données - TankFlow

Guide pour configurer PostgreSQL localement pour le développement.

## 🚀 Option 1 : Docker Compose (RECOMMANDÉ - Plus Simple)

### Prérequis
- [Docker Desktop](https://www.docker.com/products/docker-desktop) installé

### Étapes

1. **Lancer la BD avec docker-compose**:
```bash
docker-compose up -d
```

✅ Cela crée automatiquement:
- Container PostgreSQL 16 (port 5432)
- Base de données `tankflow`
- Schéma complet (migrations Flyway)

2. **Vérifier que tout fonctionne**:
```bash
docker ps  # Voir le container actif
docker logs tankflow-db  # Voir les logs
```

3. **Arrêter la BD** (quand vous ne l'utilisez plus):
```bash
docker-compose down
```

### Accès à la BD

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `tankflow`
- **Username**: `postgres`
- **Password**: `postgres`

### Outils UI (optionnel)

Utilisez **pgAdmin** ou **DBeaver** pour explorer les données:
- Connexion: `localhost:5432`
- Credentials: postgres/postgres

---

## 🔧 Option 2 : PostgreSQL Local (Installation Manuelle)

### Sur Windows

1. **Installer PostgreSQL 16+** depuis https://www.postgresql.org/download/windows/

2. **Créer la BD pendant l'installation**:
   ```
   Database Name: tankflow
   Port: 5432
   Username: postgres
   Password: [votre mot de passe]
   ```

3. **Télécharger pgAdmin** (fourni avec PostgreSQL):
   - Open pgAdmin → Connexion au serveur

4. **Créer la BD manuellement** (si nécessaire):
   ```sql
   CREATE DATABASE tankflow;
   ```

### Sur macOS

```bash
# Avec Homebrew
brew install postgresql@16
brew services start postgresql@16
createdb tankflow
```

### Sur Linux (Ubuntu/Debian)

```bash
sudo apt-get install postgresql postgresql-contrib
sudo -u postgres createdb tankflow
```

---

## 📋 Initialiser le Schéma & Données

### 1. Configuration `.env` ou `.env.dev`

Créer/modifier le fichier `.env.dev`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tankflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

### 2. Les migrations Flyway s'exécutent automatiquement

Au démarrage du backend (`mvn spring-boot:run`):
- Flyway exécute les migrations: V001 → V011
- Crée le schéma complet automatiquement
- ✅ Zéro configuration supplémentaire!

### 3. (Optionnel) Charger les données de test

Si vous voulez des données d'exemple:

```bash
# Avec Docker Compose (déjà inclus)
docker-compose up

# Ou avec PostgreSQL local
psql -U postgres -d tankflow -f src/main/resources/db/seed/001_sample_data.sql
```

---

## 🔍 Vérifier la BD

### Via pgAdmin UI (Facile)
```
1. Ouvrir http://localhost:5050
2. Login (si demandé)
3. Expand Servers → PostgreSQL → Databases → tankflow
4. Voir les tables!
```

### Via CLI
```bash
# Se connecter
psql -U postgres -d tankflow

# Lister les tables
\dt

# Voir les tanks
SELECT * FROM tank;

# Voir les cycles
SELECT * FROM cycle;

# Quitter
\q
```

### Via DBeaver (Gratuit & Puissant)
```
1. Télécharger DBeaver: https://dbeaver.io/
2. New Database Connection → PostgreSQL
3. Host: localhost, Port: 5432, DB: tankflow
4. Username: postgres, Password: postgres
5. Explorer les tables + data!
```

---

## 📊 Architecture BD

**5 tables principales:**

```
tank (25 colonnes)
├─ Identifiant unique du tank physique
└─ Clé: manufacturer_serial (UNIQUE)

cycle (15 colonnes)
├─ Aller-retour d'un tank (210 jours)
├─ FK: asset_id → tank.id
└─ Clé: public_code (UNIQUE)

expedition (10 colonnes)
├─ Regroupement logistique de cycles
└─ Clé: reference (UNIQUE)

expedition_tank (5 colonnes)
├─ Association cycle ↔ expedition
├─ FK: expedition_id, cycle_id, tank_id
└─ Clé: (expedition_id, cycle_id) UNIQUE

workflow_event (12 colonnes)
├─ Événements immuables (9 étapes/workflow)
├─ FK: cycle_id → cycle.id
└─ Clé: idempotency_key (UNIQUE)

user (6 colonnes - Optionnel)
├─ Comptes utilisateurs
└─ Clé: email (UNIQUE)
```

---

## 🧹 Nettoyer la BD

### Supprimer tout et recommencer

```bash
# Via SQL
DROP DATABASE tankflow;
CREATE DATABASE tankflow;

# Via docker-compose
docker-compose down -v  # -v supprime aussi les volumes
docker-compose up
```

---

## ⚠️ Troubleshooting

### Erreur: "FATAL: password authentication failed"

**Solution**: Vérifier les credentials dans `.env.dev`
```env
SPRING_DATASOURCE_PASSWORD=postgres  # Par défaut
```

### Erreur: "Connection refused" (Port 5432)

**Solution**: PostgreSQL n'est pas en cours d'exécution
```bash
# Avec Docker
docker-compose up

# Avec PostgreSQL local
pg_ctl start  # Windows/macOS
sudo service postgresql start  # Linux
```

### Erreur: "database tankflow does not exist"

**Solution**: Créer la BD
```bash
docker-compose down
docker-compose up  # Recrée tout
```

### Les migrations ne s'exécutent pas

**Solution**: Vérifier les logs Flyway
```bash
mvn spring-boot:run | grep -i flyway
```

---

## 🔐 Sécurité

⚠️ **DEV ONLY** - Les credentials ci-dessus sont pour le développement local SEULEMENT.

En production:
- ✅ Utiliser des variables d'env sécurisées
- ✅ Mots de passe forts
- ✅ Encryption SSL
- ✅ Backup réguliers

---

## 📝 Données d'Exemple

Le fichier `src/main/resources/db/seed/001_sample_data.sql` contient:

- ✅ 5 tanks de test (TANK-001 à TANK-005)
- ✅ 3 cycles avec workflow complet
- ✅ 3 expéditions (EN_ROUTE, SENT, LIVRÉE)
- ✅ 14 événements workflow
- ✅ 2 utilisateurs (admin + user)

**Pour charger automatiquement**: Docker Compose le fait automatiquement ✅

---

## 🎯 Résumé Rapide

**Commande unique pour démarrer**:
```bash
docker-compose up -d
```

**Voilà!** BD prête à `localhost:5432` 🚀

---

**Questions?** Consultez le README.md ou ouvrez une issue.
