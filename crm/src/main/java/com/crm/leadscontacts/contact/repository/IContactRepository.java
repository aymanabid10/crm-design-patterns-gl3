package com.crm.leadscontacts.contact.repository;

import com.crm.leadscontacts.contact.domain.Contact;
import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.shared.domain.IRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les Contacts
 * Design Pattern: Repository Pattern
 */
@Repository
public interface IContactRepository extends IRepository<Contact> {

    Optional<Contact> findByEmail(String email);

    List<Contact> findByType(ContactType type);

    List<Contact> findByAssignedTo(String userId);

    List<Contact> findByCompany(String company);

    List<Contact> findByLastInteractionDateBefore(LocalDateTime date);

    List<Contact> findByTypeAndActiveTrueOrderByLastInteractionDateDesc(ContactType type);

    long countByType(ContactType type);
}
