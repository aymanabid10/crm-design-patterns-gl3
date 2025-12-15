package com.crm.leadscontacts.lead.controller;
import com.crm.leadscontacts.lead.domain.LeadStatus;
import com.crm.leadscontacts.lead.dto.LeadCreateDTO;
import com.crm.leadscontacts.lead.dto.LeadDTO;
import com.crm.leadscontacts.lead.dto.LeadUpdateDTO;
import com.crm.leadscontacts.lead.service.ILeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des Leads
 *
 * Endpoints conformes aux User Stories de l'EPIC 3
 */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Gestion des prospects (leads)")
public class LeadController {

    private final ILeadService leadService;

    @PostMapping
    @Operation(summary = "Créer un nouveau lead",
            description = "User Story: En tant que Responsable Commercial, je peux créer et qualifier un lead")
    public ResponseEntity<LeadDTO> createLead(@Valid @RequestBody LeadCreateDTO createDTO) {
        LeadDTO created = leadService.createLead(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un lead par ID",
            description = "User Story: je peux consulter l'historique d'un lead")
    public ResponseEntity<LeadDTO> getLeadById(
            @Parameter(description = "ID du lead") @PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les leads actifs")
    public ResponseEntity<List<LeadDTO>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrer les leads par statut")
    public ResponseEntity<List<LeadDTO>> getLeadsByStatus(@PathVariable LeadStatus status) {
        return ResponseEntity.ok(leadService.getLeadsByStatus(status));
    }

    @GetMapping("/assigned/{userId}")
    @Operation(summary = "Obtenir les leads assignés à un utilisateur")
    public ResponseEntity<List<LeadDTO>> getLeadsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(leadService.getLeadsByAssignedUser(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un lead")
    public ResponseEntity<LeadDTO> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody LeadUpdateDTO updateDTO) {
        return ResponseEntity.ok(leadService.updateLead(id, updateDTO));
    }

    @PostMapping("/{id}/qualify")
    @Operation(summary = "Qualifier un lead avec un score",
            description = "User Story: je peux ajouter un score et un statut afin de prioriser le suivi")
    public ResponseEntity<LeadDTO> qualifyLead(
            @PathVariable Long id,
            @RequestParam Integer score) {
        return ResponseEntity.ok(leadService.qualifyLead(id, score));
    }

    @PostMapping("/{id}/disqualify")
    @Operation(summary = "Disqualifier un lead")
    public ResponseEntity<LeadDTO> disqualifyLead(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(leadService.disqualifyLead(id, reason));
    }

    @PostMapping("/{id}/contact")
    @Operation(summary = "Marquer le lead comme contacté")
    public ResponseEntity<LeadDTO> markAsContacted(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.markLeadAsContacted(id));
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "Convertir un lead en contact",
            description = "User Story: je peux convertir un lead en contact afin de le faire évoluer")
    public ResponseEntity<Map<String, Long>> convertToContact(@PathVariable Long id) {
        Long contactId = leadService.convertLeadToContact(id);
        return ResponseEntity.ok(Map.of("leadId", id, "contactId", contactId));
    }

    @GetMapping("/duplicates")
    @Operation(summary = "Trouver les leads en doublon",
            description = "User Story: je peux détecter et fusionner les doublons")
    public ResponseEntity<List<LeadDTO>> findDuplicates(
            @RequestParam String email,
            @RequestParam(required = false) String phone) {
        return ResponseEntity.ok(leadService.findDuplicates(email, phone));
    }

    @PostMapping("/merge")
    @Operation(summary = "Fusionner deux leads en doublon")
    public ResponseEntity<Void> mergeDuplicates(
            @RequestParam Long keepId,
            @RequestParam Long deleteId) {
        leadService.mergeDuplicates(keepId, deleteId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un lead (soft delete)")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Compter les leads par statut")
    public ResponseEntity<Map<LeadStatus, Long>> countByStatus() {
        Map<LeadStatus, Long> stats = Map.of(
                LeadStatus.NEW, leadService.countLeadsByStatus(LeadStatus.NEW),
                LeadStatus.CONTACTED, leadService.countLeadsByStatus(LeadStatus.CONTACTED),
                LeadStatus.QUALIFIED, leadService.countLeadsByStatus(LeadStatus.QUALIFIED),
                LeadStatus.UNQUALIFIED, leadService.countLeadsByStatus(LeadStatus.UNQUALIFIED),
                LeadStatus.CONVERTED, leadService.countLeadsByStatus(LeadStatus.CONVERTED)
        );
        return ResponseEntity.ok(stats);
    }
}

