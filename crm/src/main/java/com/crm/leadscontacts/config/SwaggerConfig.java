package com.crm.leadscontacts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 * Respecte les exigences de documentation du Cahier des charges
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI crmOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CRM - Gestion des Leads et Contacts API")
                        .description("API REST pour l'EPIC 3: Gestion de contacts, prospects et leads  " +
                                "Cette API implémente les fonctionnalités suivantes: " +
                                "- Gestion des Leads avec scoring et qualification " +
                                "- Gestion des Contacts " +
                                "- Gestion des Opportunités commerciales " +
                                "- Historique des interactions " +
                                "- Gestion des tâches de suivi  " +
                                "**Design Patterns appliqués:** " +
                                "- Repository Pattern " +
                                "- Service Layer Pattern " +
                                "- Observer Pattern (Notifications)  " +
                                "**Principes SOLID respectés**")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe CRM")
                                .email("crm@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement")
                ));
    }
}
