package com.crm.leadscontacts.lead.service;

import com.crm.leadscontacts.lead.domain.LeadStatus;
import com.crm.leadscontacts.lead.dto.LeadCreateDTO;
import com.crm.leadscontacts.lead.dto.LeadDTO;
import com.crm.leadscontacts.lead.dto.LeadUpdateDTO;

import java.util.List;

/**
 * Interface du service Lead
 *
 * Design Pattern: Service Layer Pattern
 * Principe SOLID: DIP - dépendance sur l'abstraction
 * Principe SOLID: ISP - interface focalisée sur les opérations Lead
 */
public interface ILeadService {

    LeadDTO createLead(LeadCreateDTO createDTO);

    LeadDTO updateLead(Long id, LeadUpdateDTO updateDTO);

    LeadDTO getLeadById(Long id);

    List<LeadDTO> getAllLeads();

    List<LeadDTO> getLeadsByStatus(LeadStatus status);

    List<LeadDTO> getLeadsByAssignedUser(String userId);

    LeadDTO qualifyLead(Long id, Integer score);

    LeadDTO disqualifyLead(Long id, String reason);

    LeadDTO markLeadAsContacted(Long id);

    Long convertLeadToContact(Long leadId);

    List<LeadDTO> findDuplicates(String email, String phone);

    void mergeDuplicates(Long keepId, Long deleteId);

    void deleteLead(Long id);

    long countLeadsByStatus(LeadStatus status);
}
