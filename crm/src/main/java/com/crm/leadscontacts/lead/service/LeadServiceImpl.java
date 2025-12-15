package com.crm.leadscontacts.lead.service;

import com.crm.leadscontacts.contact.domain.Contact;
import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.contact.repository.IContactRepository;
import com.crm.leadscontacts.lead.domain.LeadStatus;
import com.crm.leadscontacts.lead.domain.Lead;
import com.crm.leadscontacts.lead.dto.LeadCreateDTO;
import com.crm.leadscontacts.lead.dto.LeadDTO;
import com.crm.leadscontacts.lead.dto.LeadUpdateDTO;
import com.crm.leadscontacts.lead.repository.ILeadRepository;
import com.crm.leadscontacts.shared.handlers.BusinessException;
import com.crm.leadscontacts.shared.handlers.ResourceNotFoundException;
import com.crm.leadscontacts.shared.notification.INotifier;
import com.crm.leadscontacts.shared.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service Lead
 *
 * Design Pattern: Service Layer Pattern
 * Principe SOLID: SRP - logique métier des leads
 * Principe SOLID: DIP - dépend d'interfaces (ILeadRepository, INotifier)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeadServiceImpl implements ILeadService {

    private final ILeadRepository leadRepository;
    private final IContactRepository contactRepository;
    private final INotifier notifier;

    @Override
    public LeadDTO createLead(LeadCreateDTO createDTO) {
        log.info("Création d'un nouveau lead: {}", createDTO.getEmail());

        // Vérification des doublons
        leadRepository.findByEmail(createDTO.getEmail())
                .ifPresent(existing -> {
                    throw new BusinessException("Un lead avec cet email existe déjà");
                });

        Lead lead = Lead.builder()
                .firstName(createDTO.getFirstName())
                .lastName(createDTO.getLastName())
                .email(createDTO.getEmail())
                .phone(createDTO.getPhone())
                .company(createDTO.getCompany())
                .jobTitle(createDTO.getJobTitle())
                .source(createDTO.getSource())
                .assignedTo(createDTO.getAssignedTo())
                .notes(createDTO.getNotes())
                .address(createDTO.getAddress())
                .build();

        Lead saved = leadRepository.save(lead);

        notifier.sendNotification(
                createDTO.getAssignedTo(),
                "Nouveau lead assigné: " + lead.getFullName(),
                NotificationType.INFO
        );

        return mapToDTO(saved);
    }

    @Override
    public LeadDTO updateLead(Long id, LeadUpdateDTO updateDTO) {
        Lead lead = findLeadOrThrow(id);

        if (updateDTO.getFirstName() != null) lead.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null) lead.setLastName(updateDTO.getLastName());
        if (updateDTO.getPhone() != null) lead.setPhone(updateDTO.getPhone());
        if (updateDTO.getCompany() != null) lead.setCompany(updateDTO.getCompany());
        if (updateDTO.getJobTitle() != null) lead.setJobTitle(updateDTO.getJobTitle());
        if (updateDTO.getNotes() != null) lead.setNotes(updateDTO.getNotes());
        if (updateDTO.getAddress() != null) lead.setAddress(updateDTO.getAddress());

        Lead updated = leadRepository.save(lead);
        log.info("Lead {} mis à jour", id);

        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadDTO getLeadById(Long id) {
        Lead lead = findLeadOrThrow(id);
        return mapToDTO(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDTO> getAllLeads() {
        return leadRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDTO> getLeadsByStatus(LeadStatus status) {
        return leadRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDTO> getLeadsByAssignedUser(String userId) {
        return leadRepository.findByAssignedTo(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeadDTO qualifyLead(Long id, Integer score) {
        Lead lead = findLeadOrThrow(id);

        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new BusinessException("Un lead converti ne peut pas être requalifié");
        }

        lead.qualify(score);
        Lead saved = leadRepository.save(lead);

        notifier.sendNotification(
                lead.getAssignedTo(),
                "Lead qualifié: " + lead.getFullName() + " (Score: " + score + ")",
                NotificationType.LEAD_QUALIFIED
        );

        log.info("Lead {} qualifié avec score {}", id, score);
        return mapToDTO(saved);
    }

    @Override
    public LeadDTO disqualifyLead(Long id, String reason) {
        Lead lead = findLeadOrThrow(id);
        lead.disqualify(reason);
        Lead saved = leadRepository.save(lead);

        log.info("Lead {} disqualifié: {}", id, reason);
        return mapToDTO(saved);
    }

    @Override
    public LeadDTO markLeadAsContacted(Long id) {
        Lead lead = findLeadOrThrow(id);
        lead.markAsContacted();
        Lead saved = leadRepository.save(lead);

        log.info("Lead {} marqué comme contacté", id);
        return mapToDTO(saved);
    }

    @Override
    public Long convertLeadToContact(Long leadId) {
        Lead lead = findLeadOrThrow(leadId);
        System.out.println("Lead " + lead.getFullName() + " convertin");

        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new BusinessException("Ce lead a déjà été converti");
        }

        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new BusinessException("Seuls les leads qualifiés peuvent être convertis");
        }

        // Créer le contact
        Contact contact = Contact.builder()
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .jobTitle(lead.getJobTitle())
                .address(lead.getAddress())
                .type(ContactType.CUSTOMER)
                .assignedTo(lead.getAssignedTo())
                .notes("Converti depuis lead ID: " + leadId + "\\n" + lead.getNotes())
                .build();

        Contact savedContact = contactRepository.save(contact);

        // Marquer le lead comme converti
        lead.convertToContact(savedContact.getId());
        leadRepository.save(lead);

        notifier.sendNotification(
                lead.getAssignedTo(),
                "Lead converti en contact: " + lead.getFullName(),
                NotificationType.LEAD_CONVERTED
        );

        log.info("Lead {} converti en contact {}", leadId, savedContact.getId());
        return savedContact.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDTO> findDuplicates(String email, String phone) {
        return leadRepository.findByEmailOrPhone(email, phone).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void mergeDuplicates(Long keepId, Long deleteId) {
        Lead keepLead = findLeadOrThrow(keepId);
        Lead deleteLead = findLeadOrThrow(deleteId);

        // Fusionner les notes
        String mergedNotes = keepLead.getNotes() + "\\n\\n--- Fusionné avec lead " +
                deleteId + " ---\\n" + deleteLead.getNotes();
        keepLead.setNotes(mergedNotes);

        // Garder le meilleur score
        if (deleteLead.getScore() > keepLead.getScore()) {
            keepLead.setScore(deleteLead.getScore());
        }

        leadRepository.save(keepLead);
        leadRepository.delete(deleteLead);

        log.info("Leads fusionnés: {} (gardé) et {} (supprimé)", keepId, deleteId);
    }

    @Override
    public void deleteLead(Long id) {
        Lead lead = findLeadOrThrow(id);
        lead.deactivate();
        leadRepository.save(lead);
        log.info("Lead {} désactivé", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLeadsByStatus(LeadStatus status) {
        return leadRepository.countByStatus(status);
    }

    private Lead findLeadOrThrow(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", id));
    }

    private LeadDTO mapToDTO(Lead lead) {
        return LeadDTO.builder()
                .id(lead.getId())
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .jobTitle(lead.getJobTitle())
                .status(lead.getStatus())
                .source(lead.getSource())
                .score(lead.getScore())
                .address(lead.getAddress())
                .notes(lead.getNotes())
                .assignedTo(lead.getAssignedTo())
                .lastContactDate(lead.getLastContactDate())
                .qualifiedDate(lead.getQualifiedDate())
                .convertedDate(lead.getConvertedDate())
                .convertedToContactId(lead.getConvertedToContactId())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}
