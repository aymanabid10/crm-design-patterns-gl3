package com.crm.leadscontacts.lead.repository;

import com.crm.leadscontacts.lead.domain.Lead;
import com.crm.leadscontacts.lead.domain.LeadStatus;
import com.crm.leadscontacts.shared.domain.IRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les Leads
 *
 * Design Pattern: Repository Pattern
 * Principe SOLID: ISP - interface spécialisée pour Lead
 */
@Repository
public interface ILeadRepository extends IRepository<Lead> {

    List<Lead> findByStatus(LeadStatus status);

    List<Lead> findByAssignedTo(String userId);

    List<Lead> findByStatusAndAssignedTo(LeadStatus status, String userId);

    Optional<Lead> findByEmail(String email);

    List<Lead> findByScoreGreaterThanEqual(Integer minScore);

    List<Lead> findByLastContactDateBefore(LocalDateTime date);

    List<Lead> findByStatusAndActiveTrueOrderByScoreDesc(LeadStatus status);

    long countByStatus(LeadStatus status);

    // Détection de doublons
    List<Lead> findByEmailOrPhone(String email, String phone);
}
