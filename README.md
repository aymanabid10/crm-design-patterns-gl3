# CRM - Gestion des Leads et Contacts (EPIC 3)

## 📋 Description

Implémentation de l'EPIC 3 du projet CRM : Gestion de contacts, prospects et leads.

Cette application Spring Boot implémente les fonctionnalités suivantes :
- ✅ Gestion complète des Leads (création, qualification, conversion)
- ✅ Gestion des Contacts clients
- ✅ Gestion des Opportunités commerciales
- ✅ Historique des interactions
- ✅ Tâches de suivi
- ✅ Détection et fusion de doublons

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

- Java 17+
- Maven 3.8+

### Installation

``` bash
# Cloner et compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application démarre sur **http://localhost:8080**

### Accès à la documentation

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **API Docs** : http://localhost:8080/api-docs
- **Console H2** : http://localhost:8080/h2-console

## 📚 Endpoints Principaux

### Leads

```
POST   /api/leads              - Créer un lead
GET    /api/leads              - Lister tous les leads
GET    /api/leads/{id}         - Obtenir un lead
PUT    /api/leads/{id}         - Mettre à jour un lead
POST   /api/leads/{id}/qualify - Qualifier un lead
POST   /api/leads/{id}/convert - Convertir en contact
DELETE /api/leads/{id}         - Supprimer un lead
```

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
```java
public interface INotifier {
    void sendNotification(String userId, String message);
}

// Dans le service
lead.qualify(score);
notifier.sendNotification(
    lead.getAssignedTo(),
    "Lead qualifié avec score " + score
);
```

## Flux de Conversion Lead → Contact

```
1. Vérification statut (must be QUALIFIED)
2. Création Contact depuis Lead
3. Marquage Lead as CONVERTED
4. Notification utilisateur
5. Retour ID du contact créé
```

## User Stories Implémentées

| User Story | Endpoint | Design Pattern |
|------------|----------|----------------|
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
