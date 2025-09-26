# 📅 Système de Suivi de Paiements par Abonnement

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

---

## 📝 Description

Ce projet est une **application console Java** permettant la gestion centralisée des abonnements. Elle facilite le suivi des paiements, la génération automatique des échéances, la détection des impayés et la production de rapports financiers.

L'objectif principal est de fournir aux utilisateurs (personnels ou financiers) un outil complet pour gérer leurs abonnements avec ou sans engagement.

---

## 🏗️ Fonctionnalités

### 💳 Gestion des abonnements
- Créer un abonnement **avec ou sans engagement**.
- Modifier, supprimer, ou résilier un abonnement.
- Consulter la liste des abonnements.
- Filtrer les abonnements par type ou statut.
- Validation des doublons d'abonnements actifs.

### 📦 Gestion des paiements
- Enregistrer un paiement.
- Modifier ou supprimer un paiement.
- Afficher les paiements d'un abonnement.
- Détecter les **paiements manqués** et le montant total impayé.
- Afficher la somme payée d'un abonnement.
- Afficher les **5 derniers paiements**.
- Génération automatique des échéances de paiement.

### 📊 Rapports financiers
- Génération des rapports **mensuels, annuels et impayés**.
- Analyse des coûts réels et prévisionnels par abonnement.
- Statistiques détaillées par type d'abonnement.
- Répartition des paiements par type et statut.

---

## 🏗️ Architecture du projet

L'application suit une **architecture en couches** :

```
subscription-service-java/
├── src/main/java/com/subscription/manager/
│   ├── Main.java                    # Point d'entrée de l'application
│   ├── entity/                      # Classes métier
│   │   ├── Abonnement.java         # Classe abstraite des abonnements
│   │   ├── AbonnementAvecEngagement.java
│   │   ├── AbonnementSansEngagement.java
│   │   └── Paiement.java           # Entité paiement
│   ├── dao/                        # Couche d'accès aux données
│   │   ├── AbonnementDAO.java      # CRUD abonnements
│   │   └── PaiementDAO.java        # CRUD paiements
│   ├── service/                    # Logique métier
│   │   ├── AbonnementService.java  # Services abonnements
│   │   └── PaiementService.java    # Services paiements & rapports
│   ├── ui/                         # Interface utilisateur
│   │   └── ConsoleMenu.java        # Menu console interactif
│   └── util/                       # Utilitaires
│       ├── DatabaseConnection.java # Connexion base de données
│       ├── DateUtil.java          # Utilitaires de dates
│       └── ValidationUtil.java     # Validation des données
├── lib/                            # Librairies externes
│   └── mysql-connector-j-9.4.0.jar
├── docs/diagrammes/               # Documentation technique
├── build.bat                      # Script de compilation
├── run.bat                        # Script d'exécution
└── database_setup.sql            # Script de création de la base
```

---

## ⚙️ Technologies utilisées

- **Java 8+** - Langage de programmation principal
- **JDBC** - Connectivité base de données
- **MySQL** - Système de gestion de base de données
- **Programmation fonctionnelle** : Streams, Lambda, Optional, Collectors
- **Maven/Gradle** compatible pour la gestion des dépendances
- **Git** pour le contrôle de version

---

## 🛠️ Exigences techniques

- ✅ Application console Java
- ✅ Gestion des abonnements avec ou sans engagement
- ✅ Génération automatique des échéances
- ✅ Détection des paiements impayés
- ✅ Rapports financiers via Streams et Collectors
- ✅ Persistance des données via JDBC
- ✅ Gestion des exceptions avec messages clairs
- ✅ Respect de l'architecture en couches
- ✅ Validation des données d'entrée
- ✅ Interface utilisateur intuitive

---

## 📦 Base de données

### Tables principales

**Table `abonnement`**
| Champ | Type | Description |
|-------|------|-------------|
| id | VARCHAR(36) | Identifiant unique (UUID) |
| nom_service | VARCHAR(100) | Nom du service |
| montant_mensuel | DECIMAL(10,2) | Montant mensuel |
| date_debut | DATE | Date de début |
| date_fin | DATE | Date de fin |
| statut | ENUM | ACTIVE, SUSPENDU, RESILIE |
| type_abonnement | ENUM | AVEC_ENGAGEMENT, SANS_ENGAGEMENT |
| duree_engagement_mois | INT | Durée pour abonnement avec engagement |

**Table `paiement`**
| Champ | Type | Description |
|-------|------|-------------|
| id_paiement | VARCHAR(36) | Identifiant unique (UUID) |
| id_abonnement | VARCHAR(36) | Référence à l'abonnement |
| date_echeance | DATE | Date de l'échéance |
| date_paiement | DATE | Date de paiement réel |
| type_paiement | ENUM | CARTE_BANCAIRE, VIREMENT, PRELEVEMENT, PAYPAL, ESPECES |
| statut | ENUM | PAYE, NON_PAYE, EN_RETARD |
| montant | DECIMAL(10,2) | Montant du paiement |

---

## 🚀 Instructions d'installation

### Prérequis
- Java JDK 8 ou supérieur
- MySQL Server 8.0+
- Git (optionnel)

### 1. **Cloner ou télécharger le projet**
```bash
git clone https://github.com/HamzaBraik01/subscription-service-java.git
cd subscription-service-java
```

### 2. **Configurer la base de données**

Créer une base de données MySQL :
```sql
CREATE DATABASE subscription_manager;
USE subscription_manager;
```

Modifier les paramètres de connexion dans `DatabaseConnection.java` :
```java
private static final String URL = "jdbc:mysql://localhost:3306/subscription_manager";
private static final String USERNAME = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

### 3. **Compiler le projet**
```bash
# Windows
build.bat

# Ou manuellement
javac -cp "lib/*" -d bin src/main/java/com/subscription/manager/**/*.java
```

### 4. **Exécuter l'application**
```bash
# Windows
run.bat

# Ou manuellement
java -cp "bin;lib/*" com.subscription.manager.Main
```

---

## 📚 Guide d'utilisation

### Menu Principal
L'application propose un menu interactif avec les options suivantes :

1. **Gestion des Abonnements**
   - Créer des abonnements avec/sans engagement
   - Modifier, supprimer ou résilier des abonnements
   - Consulter et filtrer les abonnements

2. **Gestion des Paiements**
   - Enregistrer des paiements
   - Consulter l'historique des paiements
   - Détecter les retards de paiement
   - Calculer les montants impayés

3. **Rapports Financiers**
   - Rapport mensuel des paiements
   - Rapport annuel avec répartition
   - Analyse des impayés par abonnement

4. **Statistiques**
   - Vue d'ensemble des abonnements
   - Coûts mensuels totaux
   - Répartition par type et statut

### Exemple d'utilisation
1. Démarrer l'application
2. Choisir "1" pour créer un nouvel abonnement
3. Saisir les informations demandées
4. L'application génère automatiquement les échéances
5. Utiliser le menu paiements pour enregistrer les règlements

---

## ⚡ Points forts

- 🎯 **Architecture MVC claire** - Séparation des responsabilités
- 🔄 **Programmation fonctionnelle** - Utilisation avancée des Streams et Lambda
- 🛡️ **Validation robuste** - Contrôles de données et gestion d'erreurs
- 📊 **Rapports détaillés** - Analyses financières complètes
- 🔗 **Base de données relationnelle** - Intégrité référentielle garantie
- 🎨 **Interface intuitive** - Menu console ergonomique
- 🔄 **Génération automatique** - Échéances calculées automatiquement
- 📈 **Suivi en temps réel** - Détection automatique des retards

---

## 👨‍💻 Auteur

**Hamza Braik**
- Développeur Java
- Projet réalisé dans le cadre du module "Système de Suivi des Paiements par Abonnement"
- GitHub: [@HamzaBraik01](https://github.com/HamzaBraik01)

---

## 🤝 Contributions

Les contributions sont les bienvenues ! N'hésitez pas à :
- Signaler des bugs
- Proposer des améliorations
- Soumettre des pull requests
- Améliorer la documentation

---

![Java](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)