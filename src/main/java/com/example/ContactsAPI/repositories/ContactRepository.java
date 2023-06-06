package com.example.ContactsAPI.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ContactsAPI.models.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

}
