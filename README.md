# CRM - Gestion des Leads et Contacts (EPIC 3)

## 📋 Description

Implémentation de l'EPIC 3 du projet CRM : Gestion de contacts, prospects et leads.

Cette application Full-Stack (Spring Boot + React) implémente les fonctionnalités suivantes :

### Backend (Spring Boot)

- ✅ Gestion complète des Leads (création, qualification, conversion)
- ✅ Gestion des Contacts clients
- ✅ Gestion des Opportunités commerciales
- ✅ Historique des interactions
- ✅ Tâches de suivi
- ✅ Détection et fusion de doublons

### Frontend (React)

- ✅ Interface moderne avec React 19 + TypeScript
- ✅ Dashboard avec statistiques en temps réel
- ✅ Gestion CRUD complète des Leads et Contacts
- ✅ Qualification et conversion de leads
- ✅ Design responsive avec Tailwind CSS v4

## 🏗️ Architecture

### Design Patterns Appliqués

1. **Repository Pattern**

   - Isolation de la couche de persistance
   - Interface \`IRepository<T>\` générique

2. **Service Layer Pattern**

   - Logique métier centralisée
   - Interfaces et implémentations séparées

3. **Observer Pattern**
   - Système de notifications (\`INotifier\`)
   - Alertes proactives

### Principes SOLID

- **SRP** : Chaque classe a une responsabilité unique
- **OCP** : Extensions sans modification du code existant
- **LSP** : Substitution des implémentations
- **ISP** : Interfaces spécialisées
- **DIP** : Dépendances sur abstractions

## 🚀 Démarrage

### Prérequis

**Backend:**

- Java 17+
- Maven 3.8+
- MySQL 8.0+ (ou H2 pour développement)

**Frontend:**

- Node.js 18+
- npm 9+

### Installation

#### Backend (Spring Boot)

```bash
# Naviguer vers le dossier backend
cd crm

# Configuration de la base de données
# Créer le fichier src/main/resources/application.properties
# (voir application.properties.example)

# Compiler et lancer
mvn clean install
mvn spring-boot:run
```

Le backend démarre sur **http://localhost:8080**

#### Frontend (React)

```bash
# Naviguer vers le dossier frontend
cd crm-frontend

# Installer les dépendances
npm install

# Configurer l'environnement
# Créer le fichier .env
# VITE_API_URL=http://localhost:8080

# Lancer le serveur de développement
npm run dev
```

## 🗂️ Structure du Projet

### Backend (Spring Boot)

```
crm/src/main/java/com/crm/leadscontacts/
├── shared/              # Package partagé (Core)
│   ├── domain/          # BaseEntity, Address
│   ├── repository/      # IRepository
│   ├── notification/    # INotifier, NotificationService
│   └── handlers/        # Exceptions personnalisées
├── lead/                # Module Lead
│   ├── domain/          # Entités Lead
│   ├── repository/      # Repositories
│   ├── service/         # Services métier
│   ├── controller/      # REST Controllers
│   └── dto/             # Data Transfer Objects
├── contact/             # Module Contact
│   └── (same structure)
└── config/              # Configuration Spring & CORS
```

### Frontend (React + TypeScript)

````
crm-frontend/
├── src/
│   ├── components/      # Composants réutilisables
│   │   └── AppLayout.tsx    # Layout avec navigation
│   ├── pages/           # Pages de l'application
│   │   ├── LeadsPage.tsx    # Gestion des Leads
│   │   └── ContactsPage.tsx # Gestion des Contacts
│   ├── services/        # API & Services
│   │   └── api.ts           # Client Axios + Types
│   ├── App.tsx          # Router principal
│   ├── main.tsx         # Point d'entrée
│   └── index.css        # Styles Tailwind
├── public/              # Assets statiques
├── vite.config.ts       # Configuration Vite
├── tailwind.config.js   # Configuration Tailwind
└── package.json         # Dépendances npm
```T   /api/leads              - Créer un lead
GET    /api/leads              - Lister tous les leads
GET    /api/leads/{id}         - Obtenir un lead
PUT    /api/leads/{id}         - Mettre à jour un lead
POST   /api/leads/{id}/qualify - Qualifier un lead
POST   /api/leads/{id}/convert - Convertir en contact
DELETE /api/leads/{id}         - Supprimer un lead
````

### Contacts

```
POST   /api/contacts           - Créer un contact
GET    /api/contacts           - Lister tous les contacts
GET    /api/contacts/{id}      - Obtenir un contact
PUT    /api/contacts/{id}      - Mettre à jour un contact
DELETE /api/contacts/{id}      - Supprimer un contact
```

## 🗂️ Structure du Projet

```
src/main/java/com/crm/leadscontacts/
├── shared/              # Package partagé (Core)
│   ├── domain/          # BaseEntity, Address,
│   ├── repository/      # IRepository
│   ├── notification/    # INotifier, NotificationService
│   └── handlers/        # Exceptions personnalisées
├── lead/                # Module Lead
│   ├── domain/          # Entités Lead
│   ├── repository/      # Repositories
│   ├── service/         # Services métier
│   ├── controller/      # REST Controllers
│   └── dto/             # Data Transfer Objects
├── contact/             # Module Contact
└── config/              # Configuration Spring
```

# Architecture Technique - CRM EPIC 3

## Vue d'ensemble

Ce document détaille l'architecture de l'EPIC 3 : Gestion de contacts, prospects et leads.

## Diagramme de Classes

### Package Shared (Core)

```
┌─────────────────────┐
│   <<abstract>>      │
│    BaseEntity       │
├─────────────────────┤
│ - id: Long          │
│ - createdAt: Date   │
│ - updatedAt: Date   │
│ - active: Boolean   │
└─────────────────────┘
        △
        │ (extends)
        │
    ┌───┴───┐
    │       │
┌───┴───┐ ┌─┴──────┐
│ Lead  │ │Contact │
└───────┘ └────────┘
```

### Design Patterns Détaillés

#### 1. Repository Pattern

**Problème** : Séparer la logique métier de la logique de persistance

**Solution** :

```java
// Interface générique
public interface IRepository<T extends BaseEntity> {
    List<T> findByActiveTrue();
    Optional<T> findByIdAndActiveTrue(Long id);
}

// Spécialisation
public interface ILeadRepository extends IRepository<Lead> {
    List<Lead> findByStatus(LeadStatus status);
}
```

**Avantages** :

- ✅ Respect du DIP (Dependency Inversion Principle)
- ✅ Tests facilités avec mocks
- ✅ Changement de BD transparent

#### 2. Service Layer Pattern

**Problème** : Centraliser la logique métier

**Solution** :

```java
public interface ILeadService {
    LeadDTO qualifyLead(Long id, Integer score);
    Long convertLeadToContact(Long leadId);
}

@Service
public class LeadServiceImpl implements ILeadService {
    private final ILeadRepository repository;
    private final INotifier notifier;

    // Logique métier ici
}
```

**Avantages** :

- ✅ SRP : une seule responsabilité par service
- ✅ Transactions gérées au bon niveau
- ✅ Orchestration des dépendances

#### 3. Observer Pattern

**Problème** : Notifier les utilisateurs d'événements métier

**Solution** :

````java
public interface INotifier {
    void sendNotification(String userId, String message);
}

// Dans le service
lead.qualify(score);
notifier.sendNotification(
    lead.getAssignedTo(),
## ⚙️ Configuration

### Backend (Spring Boot)

Créer le fichier `crm/src/main/resources/application.properties` (voir `application.properties.example`)

#### H2 (Développement)
```properties
spring.datasource.url=jdbc:h2:mem:crmdb
spring.jpa.hibernate.ddl-auto=create-drop
````

#### MySQL (Production)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

### Frontend (React)

Créer le fichier `crm-frontend/.env`

```env
VITE_API_URL=http://localhost:8080
```

## 🛠️ Technologies Utilisées

### Backend

- **Spring Boot 4.0.0** - Framework Java
- **Spring Data JPA** - Persistence
- **MySQL 8.0** - Base de données
- **Lombok** - Réduction du code boilerplate
- **Swagger/OpenAPI** - Documentation API
- **Maven** - Gestion des dépendances

### Frontend

- **React 19** - Library UI
- **TypeScript 5.9** - Typage statique
- **Vite 7.2** - Build tool & dev server
- **Tailwind CSS v4** - Styling moderne
- **TanStack Query v5** - Gestion d'état serveur
- **React Router v7** - Navigation
- **Axios 1.13** - Client HTTP

## 🎯 Fonctionnalités Frontend

### Page Leads

- 📊 Dashboard avec 4 KPIs (Total, Nouveaux, Qualifiés, Convertis)
- ➕ Création de leads avec formulaire modal
- ✅ Qualification de leads avec attribution de score
- 🔄 Conversion de leads en contacts
- 🗑️ Suppression avec confirmation
- 🎨 Badges de statut colorés (NEW, QUALIFIED, CONVERTED, etc.)
- 📱 Design responsive

### Page Contacts

- 📊 Dashboard avec 4 KPIs (Total, Actifs, Clients, Partenaires)
- ➕ Création de contacts avec formulaire modal
- 👥 Visualisation des informations de contact
- 🏷️ Badges de type (CUSTOMER, PARTNER, VENDOR)
- 🗑️ Suppression avec confirmation
- 📱 Design responsive

### Fonctionnalités Communes

- ⚡ Chargement optimisé avec React Query
- 🔄 Invalidation automatique du cache après mutations
- ⏳ États de chargement avec spinners
- ❌ Gestion d'erreurs avec messages informatifs
- 📭 États vides avec call-to-action
- 🎨 UI moderne avec Tailwind CSS v4

## 📄 Licence

MIT License--|----------|----------------|
| Créer un lead | POST /api/leads | Repository + Service |
| Qualifier un lead | POST /api/leads/{id}/qualify | Service Layer |
| Convertir en contact | POST /api/leads/{id}/convert | Service Layer + Observer |
| Détecter doublons | GET /api/leads/duplicates | Repository |
| Fusionner doublons | POST /api/leads/merge | Service Layer |

## Configuration Base de Données

Créer le fichier `ressources/application.properties`, vous trouvez un fichier example pour tout le setup de l'application intitulé `ressources/application.properties.example`

### H2 (Développement)

```properties
spring.datasource.url=jdbc:h2:mem:crmdb
spring.jpa.hibernate.ddl-auto=create-drop
```

### MySQL (Production)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm
spring.jpa.hibernate.ddl-auto=validate
```

## 📄 Licence

MIT License
