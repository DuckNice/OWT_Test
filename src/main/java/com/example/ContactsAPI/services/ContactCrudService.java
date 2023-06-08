package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.models.contact.DTOContact;
import com.example.ContactsAPI.repositories.ContactRepository;

@Service
public class ContactCrudService extends CrudService<DBContact, DTOContact, ContactRepository> {

    @Autowired
    SkillCrudService skillsService;

    protected DBContact dbEntryFromDTO(DTOContact dto) {
        DBContact newContact = new DBContact();
        newContact.setEmail(dto.getEmail());
        newContact.setFirstName(dto.getFirstName());
        newContact.setLastName(dto.getLastName());
        newContact.setFullName(dto.getLastName().toUpperCase() + " " + dto.getFirstName());
        newContact.setAddress(dto.getAddress());
        newContact.setMobileNumber(dto.getMobileNumber());

        return newContact;
    }

    protected boolean dbConflictValidation(DTOContact dto, Long id) {
        Optional<DBContact> existingEmailContact = repo.findByEmail(dto.getEmail());

        return existingEmailContact.isEmpty() || existingEmailContact.get().getId() == id;
    }

    public Optional<DBContact> createWUnknownId(DTOContact creationEntry) {
        // Create the annotated skills
        // Set<DBSkill> skills =
        // skillsService.createSkillsListFromDTO(creationEntry.getSkills());

        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, -1L)) {
            return Optional.<DBContact>empty();
        }

        // Save and return
        DBContact newEntry = dbEntryFromDTO(creationEntry);
        // newEntry.setSkills(skills);
        return Optional.of((repo.save(newEntry)));
    }

    /// Returns touple. true = updated, false = created.
    /// No return entry indicates a conflict w/ the data layer and the entry wasn't
    /// changed.
    public Pair<Boolean, Optional<DBContact>> createOrUpdateWKnownId(long id, DTOContact creationEntry) {
        // Create the annotated skills
        // Set<DBSkill> skills =
        // skillsService.createSkillsListFromDTO(creationEntry.getSkills());

        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, id)) {
            return Pair.of(false, Optional.<DBContact>empty());
        }

        // Check if the entry exists
        Optional<DBContact> existingEntry = repo.findById(id);

        // Save and return
        DBContact newEntry = dbEntryFromDTO(creationEntry);
        // newEntry.setSkills(skills);
        return Pair.of(existingEntry.isPresent(), Optional.of((repo.save(newEntry))));
    }
}
