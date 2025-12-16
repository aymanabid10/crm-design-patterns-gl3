package com.crm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Application principale du CRM - EPIC 3: Gestion des Leads et Contacts
 *
 * @author Équipe CRM
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class CrmApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(CrmApplication.class, args);
        System.out.println("  CRM Leads & Contacts API Started");
        System.out.println("  Swagger UI: http://localhost:8080/swagger-ui.html");
    }
}