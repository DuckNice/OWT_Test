package com.example.ContactsAPI.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.DTOSkill;
import com.example.ContactsAPI.repositories.SkillRepository;

@Service
public class SkillCrudService extends CrudService<DBSkill, DTOSkill, SkillRepository> {

    private DBSkill dbEntryFromDTO(DTOSkill dto) {
        DBSkill newSkill = new DBSkill();
        newSkill.setName(dto.getName());
        newSkill.setLevel(dto.getSkill());

        return newSkill;
    }

    private boolean dbConflictValidation(DTOSkill dto, Long id) {
        Optional<DBSkill> existingNamedSkill = repo.findByNameAndLevel(dto.getName(), dto.getSkill());

        return existingNamedSkill.isEmpty() || existingNamedSkill.get().getId() == id;
    }

    @Override
    public Optional<DBSkill> createWUnknownId(DTOSkill creationEntry) {
        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, -1L)) {
            return Optional.<DBSkill>empty();
        }

        // Save and return
        DBSkill newEntry = dbEntryFromDTO(creationEntry);
        return Optional.of((repo.save(newEntry)));
    }

    /// Returns touple. true = updated, false = created.
    /// No return entry indicates a conflict w/ the data layer and the entry wasn't
    /// changed.
    @Override
    public Pair<Boolean, Optional<DBSkill>> createOrUpdateWKnownId(long id, DTOSkill creationEntry) {
        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, id)) {
            return Pair.of(false, Optional.<DBSkill>empty());
        }

        // Check if the entry exists
        Optional<DBSkill> existingEntry = repo.findById(id);

        // Save and return
        DBSkill newEntry = dbEntryFromDTO(creationEntry);
        return Pair.of(existingEntry.isPresent(), Optional.of((repo.save(newEntry))));
    }

    public Set<DBSkill> createSkillsListFromDTO(DTOSkill[] skills) {
        if (skills == null) {
            return new HashSet<DBSkill>();
        }

        return Arrays.stream(skills)
                .<DBSkill>map((DTOSkill dto) -> {
                    Optional<DBSkill> oldSkill = repo.findByNameAndLevel(dto.getName(), dto.getSkill());

                    return oldSkill.isPresent() ? oldSkill.get() : repo.save(dbEntryFromDTO(dto));
                })
                .collect(Collectors.toSet());
    }
}
