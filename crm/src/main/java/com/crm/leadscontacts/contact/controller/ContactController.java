package com.crm.leadscontacts.contact.controller;

import com.crm.leadscontacts.contact.service.IContactService;
import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.contact.dto.ContactCreateDTO;
import com.crm.leadscontacts.contact.dto.ContactDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Gestion des contacts clients")
public class ContactController {

    private final IContactService contactService;

    @PostMapping
    @Operation(summary = "Créer un nouveau contact")
    public ResponseEntity<ContactDTO> createContact(@Valid @RequestBody ContactCreateDTO createDTO) {
        ContactDTO created = contactService.createContact(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un contact par ID")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les contacts")
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Filtrer par type de contact")
    public ResponseEntity<List<ContactDTO>> getContactsByType(@PathVariable ContactType type) {
        return ResponseEntity.ok(contactService.getContactsByType(type));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un contact")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactCreateDTO updateDTO) {
        return ResponseEntity.ok(contactService.updateContact(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contact")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

