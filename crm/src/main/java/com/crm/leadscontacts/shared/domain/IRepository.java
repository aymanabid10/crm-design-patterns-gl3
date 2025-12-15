package com.crm.leadscontacts.shared.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour tous les repositories du CRM
 *
 * Design Pattern: Repository Pattern
 * Principe SOLID: DIP - dépendance sur l'abstraction, pas sur l'implémentation
 * Principe SOLID: ISP - interface spécialisée et cohérente
 */

@NoRepositoryBean
public interface IRepository<T> extends JpaRepository<T, Long> {

    /**
     * Trouve toutes les entités actives
     */
    List<T> findByActiveTrue();

    /**
     * Trouve une entité active par ID
     */
    Optional<T> findByIdAndActiveTrue(Long id);

    /**
     * Compte les entités actives
     */
    long countByActiveTrue();
}
