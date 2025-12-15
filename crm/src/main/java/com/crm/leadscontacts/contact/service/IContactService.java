package com.crm.leadscontacts.contact.service;

import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.contact.dto.ContactCreateDTO;
import com.crm.leadscontacts.contact.dto.ContactDTO;

import java.util.List;

/**
 * Interface du service Contact
 * Design Pattern: Service Layer Pattern
 */
public interface IContactService {

    ContactDTO createContact(ContactCreateDTO createDTO);

    ContactDTO updateContact(Long id, ContactCreateDTO updateDTO);

    ContactDTO getContactById(Long id);

    List<ContactDTO> getAllContacts();

    List<ContactDTO> getContactsByType(ContactType type);

    List<ContactDTO> getContactsByAssignedUser(String userId);

    void deleteContact(Long id);

    long countContactsByType(ContactType type);
}
