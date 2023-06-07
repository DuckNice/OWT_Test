package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Pair;

import com.example.ContactsAPI.models.DBObject;
import com.example.ContactsAPI.models.DTObject;

//Implements base crud features for endpoints in isolation
public abstract class CrudService<DB extends DBObject, DT extends DTObject, R extends CrudRepository<DB, Long>> {
    @Autowired
    protected R repo;

    protected abstract DB dbEntryFromDTO(DT dto);

    protected abstract boolean dbConflictValidation(DT dto, Long id);

    public Optional<DB> createWUnknownId(DT creationEntry) {
        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, -1L)) {
            return Optional.<DB>empty();
        }

        // Save and return
        DB newEntry = dbEntryFromDTO(creationEntry);
        return Optional.of((repo.save(newEntry)));
    }

    /// Returns touple. true = updated, false = created.
    /// No return entry indicates a conflict w/ the data layer and the entry wasn't
    /// changed.
    public Pair<Boolean, Optional<DB>> createOrUpdateWKnownId(long id, DT creationEntry) {
        // Check if there's a conflict with existing db structures
        if (!dbConflictValidation(creationEntry, id)) {
            return Pair.of(false, Optional.<DB>empty());
        }

        // Check if the entry exists
        Optional<DB> existingEntry = repo.findById(id);

        // Save and return
        DB newEntry = dbEntryFromDTO(creationEntry);
        return Pair.of(existingEntry.isPresent(), Optional.of((repo.save(newEntry))));
    }
}
