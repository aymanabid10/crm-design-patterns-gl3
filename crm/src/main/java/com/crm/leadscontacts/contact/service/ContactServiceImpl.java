package com.crm.leadscontacts.contact.service;

import com.crm.leadscontacts.contact.domain.Contact;
import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.contact.dto.ContactCreateDTO;
import com.crm.leadscontacts.contact.dto.ContactDTO;
import com.crm.leadscontacts.contact.repository.IContactRepository;
import com.crm.leadscontacts.shared.handlers.BusinessException;
import com.crm.leadscontacts.shared.handlers.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service Contact
 * Design Pattern: Service Layer Pattern
 * Principe SOLID: SRP, DIP
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements IContactService {

    private final IContactRepository contactRepository;

    @Override
    public ContactDTO createContact(ContactCreateDTO createDTO) {
        log.info("Création d'un nouveau contact: {}", createDTO.getEmail());

        contactRepository.findByEmail(createDTO.getEmail())
                .ifPresent(existing -> {
                    throw new BusinessException("Un contact avec cet email existe déjà");
                });

        Contact contact = Contact.builder()
                .firstName(createDTO.getFirstName())
                .lastName(createDTO.getLastName())
                .email(createDTO.getEmail())
                .phone(createDTO.getPhone())
                .company(createDTO.getCompany())
                .jobTitle(createDTO.getJobTitle())
                .type(createDTO.getType())
                .address(createDTO.getAddress())
                .assignedTo(createDTO.getAssignedTo())
                .notes(createDTO.getNotes())
                .build();

        Contact saved = contactRepository.save(contact);
        return mapToDTO(saved);
    }

    @Override
    public ContactDTO updateContact(Long id, ContactCreateDTO updateDTO) {
        Contact contact = findContactOrThrow(id);

        contact.setFirstName(updateDTO.getFirstName());
        contact.setLastName(updateDTO.getLastName());
        contact.setPhone(updateDTO.getPhone());
        contact.setCompany(updateDTO.getCompany());
        contact.setJobTitle(updateDTO.getJobTitle());
        contact.setType(updateDTO.getType());
        contact.setAddress(updateDTO.getAddress());
        contact.setNotes(updateDTO.getNotes());

        Contact updated = contactRepository.save(contact);
        log.info("Contact {} mis à jour", id);

        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDTO getContactById(Long id) {
        return mapToDTO(findContactOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getAllContacts() {
        return contactRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getContactsByType(ContactType type) {
        return contactRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getContactsByAssignedUser(String userId) {
        return contactRepository.findByAssignedTo(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteContact(Long id) {
        Contact contact = findContactOrThrow(id);
        contact.deactivate();
        contactRepository.save(contact);
        log.info("Contact {} désactivé", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countContactsByType(ContactType type) {
        return contactRepository.countByType(type);
    }

    private Contact findContactOrThrow(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", id));
    }

    private ContactDTO mapToDTO(Contact contact) {
        return ContactDTO.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .company(contact.getCompany())
                .jobTitle(contact.getJobTitle())
                .type(contact.getType())
                .address(contact.getAddress())
                .notes(contact.getNotes())
                .assignedTo(contact.getAssignedTo())
                .lastInteractionDate(contact.getLastInteractionDate())
                .lifetimeValue(contact.getLifetimeValue())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
