package com.example.ContactsAPI.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ContactsAPI.models.DBContact;

@Repository
public interface ContactRepository extends CrudRepository<DBContact, Long> {
    Optional<DBContact> findByEmail(String emailAddress);
}
