# 🏦 BankGuard Transaction Analyzer

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![JDBC](https://img.shields.io/badge/JDBC-Enabled-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Application Java d'analyse intelligente des transactions bancaires avec détection automatique d'anomalies**

[Fonctionnalités](#-fonctionnalités-clés) • [Installation](#-installation) • [Utilisation](#-utilisation) • [Architecture](#-architecture) • [Documentation](#-documentation)

</div>

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités clés](#-fonctionnalités-clés)
- [Architecture](#-architecture)
- [Technologies utilisées](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)


---

## 🎯 À propos

**BankGuard Transaction Analyzer** est une application Java robuste conçue pour la gestion et l'analyse avancée des transactions bancaires. Elle offre une solution complète pour détecter les anomalies financières, générer des rapports détaillés et surveiller l'activité des comptes en temps réel.

### Contexte

Dans un environnement bancaire moderne, des milliers de transactions sont effectuées quotidiennement. BankGuard répond au besoin critique de :
- Centraliser et organiser les informations bancaires
- Détecter automatiquement les transactions suspectes
- Identifier les comptes inactifs et les comportements inhabituels
- Produire des rapports analytiques pour une meilleure prise de décision

---

## ✨ Fonctionnalités clés

### 🔐 Gestion des Clients
- Création, modification et suppression de clients
- Recherche avancée par ID ou nom
- Vue d'ensemble des soldes et comptes associés

### 💳 Gestion des Comptes
- Support de multiples types de comptes (Courant, Épargne)
- Gestion du découvert autorisé et des taux d'intérêt
- Recherche et tri des comptes par divers critères

### 💸 Analyse des Transactions
- Enregistrement de toutes les opérations (Versement, Retrait, Virement)
- Filtrage intelligent par montant, type, date et lieu
- Regroupement et agrégation des données
- Calcul automatique des moyennes et totaux

### 🚨 Détection d'Anomalies
- **Transactions à montant élevé** : Détection automatique au-delà d'un seuil configurable
- **Localisation suspecte** : Identification des opérations depuis des lieux inhabituels
- **Fréquence excessive** : Repérage des transactions multiples en très peu de temps
- **Comptes inactifs** : Alertes sur les comptes sans activité prolongée

### 📊 Rapports et Statistiques
- Top 5 des clients par solde total
- Rapports mensuels avec ventilation par type de transaction
- Analyse des volumes et tendances
- Export des données (CSV/JSON)

---

## 🏗️ Architecture

BankGuard suit une **architecture en couches** respectant les principes SOLID :

```
┌─────────────────────────────────────┐
│     Couche Présentation (UI)       │
│         Menu Console                │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Couche Services (Métier)       │
│  ClientService | CompteService      │
│  TransactionService | RapportService│
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│         Couche DAO (Accès Data)     │
│    ClientDAO | CompteDAO            │
│    TransactionDAO                   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Couche Entity (Modèle)         │
│  Client | Compte | Transaction      │
│  (Records & Sealed Classes)         │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Base de Données (JDBC)         │
│      MySQL / PostgreSQL             │
└─────────────────────────────────────┘
```

### Principes de conception

- **Séparation des responsabilités** : Chaque couche a un rôle bien défini
- **Immutabilité** : Utilisation de `record` pour les entités
- **Hiérarchie fermée** : `sealed class` pour les types de comptes
- **Programmation fonctionnelle** : Stream API, Optional, Lambdas

---

## 🛠️ Technologies utilisées

| Technologie | Version | Usage |
|------------|---------|-------|
| **Java** | 17 | Langage principal avec Records, Sealed Classes, Pattern Matching |
| **JDBC** | 4.0+ | Persistance et accès aux données |
| **MySQL** | 8.0+ | Base de données relationnelle |
| **Stream API** | Java 17 | Programmation fonctionnelle et traitement de données |
| **Git** | 2.x | Gestion de versions |

### Fonctionnalités Java 17 utilisées
- ✅ **Records** pour les entités immutables
- ✅ **Sealed Classes** pour la hiérarchie des comptes
- ✅ **Switch Expressions** pour la logique conditionnelle
- ✅ **Pattern Matching** pour le casting
- ✅ **Text Blocks** pour les requêtes SQL
- ✅ **var** pour l'inférence de type

---

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java JDK 17** ou supérieur
  ```bash
  java -version
  ```

- **MySQL 8.0+** ou **PostgreSQL 13+**
  ```bash
  mysql --version
  ```

- **Git** pour le clonage du projet
  ```bash
  git --version
  ```

- Un IDE Java (IntelliJ IDEA, Eclipse, VS Code avec extensions Java)

---

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/votre-username/bankguard-transaction-analyzer.git
cd bankguard-transaction-analyzer
```

### 2. Créer la base de données

```sql
CREATE DATABASE bankguard_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bankguard_db;

-- Les tables seront créées automatiquement au premier lancement
-- Ou exécutez le script : source database/schema.sql
```

### 3. Compiler le projet

```bash
javac -d out src/**/*.java
```

### 4. Créer le JAR exécutable

```bash
jar cvfe BankGuard.jar com.bankguard.Main -C out .
```

---

## ⚙️ Configuration

Créez un fichier `config.properties` à la racine du projet :

```properties
# Configuration Base de Données
db.url=jdbc:mysql://localhost:3306/bankguard_db
db.username=votre_username
db.password=votre_password
db.driver=com.mysql.cj.jdbc.Driver

# Configuration Anomalies
anomaly.threshold.amount=10000.0
anomaly.threshold.frequency=5
anomaly.threshold.timeframe=60

# Configuration Rapports
report.export.path=./reports
report.format=CSV
```

> ⚠️ **Important** : Ne commitez jamais vos identifiants réels ! Utilisez `.env` ou `config.properties` (déjà dans .gitignore)

---

## 💻 Utilisation

### Lancer l'application

```bash
java -jar BankGuard.jar
```

### Menu principal

```
╔════════════════════════════════════════════════╗
║       🏦 BANKGUARD TRANSACTION ANALYZER       ║
╠════════════════════════════════════════════════╣
║  1. 👤 Gestion des Clients                     ║
║  2. 💳 Gestion des Comptes                     ║
║  3. 💸 Enregistrer une Transaction             ║
║  4. 📊 Consulter l'Historique                  ║
║  5. 📈 Analyses et Rapports                    ║
║  6. 🚨 Détection d'Anomalies                   ║
║  7. ⚙️  Paramètres                             ║
║  0. 🚪 Quitter                                 ║
╚════════════════════════════════════════════════╝
```

### Exemples d'utilisation

#### Créer un client et un compte
```
> 1 (Gestion des Clients)
> 1 (Nouveau Client)
> Nom : Jean Dupont
> Email : jean.dupont@email.com
✓ Client créé avec succès (ID: 1)

> 2 (Gestion des Comptes)
> 1 (Nouveau Compte)
> Type : Compte Courant
> Découvert autorisé : 500.00 €
✓ Compte créé : FR76 XXXX XXXX XXXX
```

#### Enregistrer une transaction
```
> 3 (Enregistrer Transaction)
> Numéro de compte : FR76XXXXXXXXXXXXXXXX
> Type : VERSEMENT
> Montant : 1500.00 €
> Lieu : Paris, France
✓ Transaction enregistrée avec succès
```

#### Détecter les anomalies
```
> 6 (Détection d'Anomalies)
> Analyse en cours...

⚠️ ALERTES DÉTECTÉES :
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🚨 Transaction suspecte (Montant élevé)
   Compte : FR76XXXXXXXXXXXXXXXX
   Montant : 25,000.00 €
   Date : 2025-09-27 14:32:18
   
🚨 Localisation inhabituelle
   Client : Marie Martin
   Lieu : Tokyo, Japan (habituel : Paris, France)
```

---

## 📂 Structure du projet

```
bankguard-transaction-analyzer/
│
├── 📁 src/
│   └── com/bankguard/
│       ├── 📁 entity/              # Modèles de données
│       │   ├── Client.java         # Record
│       │   ├── Compte.java         # Sealed class
│       │   ├── CompteCourant.java
│       │   ├── CompteEpargne.java
│       │   └── Transaction.java    # Record
│       │
│       ├── 📁 dao/                 # Accès aux données
│       │   ├── ClientDAO.java
│       │   ├── CompteDAO.java
│       │   └── TransactionDAO.java
│       │
│       ├── 📁 service/             # Logique métier
│       │   ├── ClientService.java
│       │   ├── CompteService.java
│       │   ├── TransactionService.java
│       │   └── RapportService.java
│       │
│       ├── 📁 ui/                  # Interface utilisateur
│       │   └── ConsoleMenu.java
│       │
│       ├── 📁 util/                # Utilitaires
│       │   ├── DatabaseUtil.java
│       │   ├── DateUtil.java
│       │   └── ValidationUtil.java
│       │
│       └── Main.java               # Point d'entrée
│
├── 📁 database/
│   ├── schema.sql                  # Structure de la BDD
│   └── data.sql                    # Données de test
│
├── 📁 docs/
│   ├── class-diagram.png           # Diagramme de classes
│   └── architecture.md             # Documentation technique
│
├── 📁 reports/                     # Rapports générés
│
├── .gitignore
├── README.md
├── LICENSE
└── config.properties.example       # Template de configuration
```

---


