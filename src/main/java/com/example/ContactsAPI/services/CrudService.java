package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Pair;

//Implements base crud features for endpoints in isolation
public abstract class CrudService<DB, DT, R extends CrudRepository<DB, Long>> {
    @Autowired
    protected R repo;

    public abstract Optional<DB> createWUnknownId(DT creationEntry);

    public abstract Pair<Boolean, Optional<DB>> createOrUpdateWKnownId(long id, DT creationEntry);
}
