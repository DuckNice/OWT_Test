package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.contact.ContactForCreation;
import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;

@Service
public class ContactCrudService extends CrudService<DBContact, ContactForCreation, ContactRepository> {

    @Override
    protected DBContact dbEntryFromDTO(ContactForCreation dto) {
        DBContact newContact = new DBContact();
        newContact.setEmail(dto.getEmail());
        newContact.setFirstName(dto.getFirstName());
        newContact.setLastName(dto.getLastName());
        newContact.setFullName(dto.getLastName().toUpperCase() + " " + dto.getFirstName());
        newContact.setAddress(dto.getAddress());
        newContact.setMobileNumber(dto.getMobileNumber());

        return newContact;
    }

    @Override
    protected boolean dbConflictValidation(ContactForCreation dto, Long id) {
        Optional<DBContact> existingEmailContact = repo.findByEmail(dto.getEmail());

        return existingEmailContact.isEmpty() || existingEmailContact.get().getId() == id;
    }
}
