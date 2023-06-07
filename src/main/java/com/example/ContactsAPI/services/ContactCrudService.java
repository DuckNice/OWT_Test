package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.ContactForCreation;
import com.example.ContactsAPI.models.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;

@Service
public class ContactCrudService {
    @Autowired
    ContactRepository contactRepo;

    private DBContact dbContactFromDTO(ContactForCreation creationContact) {
        DBContact newContact = new DBContact();
        newContact.setEmail(creationContact.getEmail());
        newContact.setFirstName(creationContact.getFirstName());
        newContact.setLastName(creationContact.getLastName());
        newContact.setFullName(creationContact.getLastName().toUpperCase() + " " + creationContact.getFirstName());
        newContact.setAddress(creationContact.getAddress());
        newContact.setMobileNumber(creationContact.getMobileNumber());

        return newContact;
    }

    public Optional<DBContact> createWUnknownId(ContactForCreation creationContact) {
        // Check the contact doesn't exist
        Optional<DBContact> existingContact = contactRepo.findByEmail(creationContact.getEmail());
        if (existingContact.isPresent()) {
            return Optional.<DBContact>empty();
        }

        // Save and return
        DBContact newContact = dbContactFromDTO(creationContact);
        return Optional.of((contactRepo.save(newContact)));
    }

    /// Returns touple. true = updated, false = created.
    /// No return entry indicates a conflict w/ the data layer and the entry wasn't
    /// changed.
    public Pair<Boolean, Optional<DBContact>> createOrUpdateWKnownId(long id, ContactForCreation creationContact) {
        // Check the email isn't used
        Optional<DBContact> existingEmailContact = contactRepo.findByEmail(creationContact.getEmail());

        if (existingEmailContact.isPresent() && existingEmailContact.get().getId() != id) {
            // The email is already used, and not by this user. return failure
            return Pair.of(false, Optional.<DBContact>empty());
        }

        // Check if the user exists
        Optional<DBContact> existingContact = contactRepo.findById(id);

        // Save and return
        DBContact newContact = dbContactFromDTO(creationContact);
        return Pair.of(existingContact.isPresent(), Optional.of((contactRepo.save(newContact))));
    }
}
