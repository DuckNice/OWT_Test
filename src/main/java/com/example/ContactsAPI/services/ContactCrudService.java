package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.ContactForCreation;
import com.example.ContactsAPI.models.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;

@Service
public class ContactCrudService {
    @Autowired
    ContactRepository contactRepo;

    public Optional<DBContact> createWUnknownId(ContactForCreation creationContact) {
        // Check the contact doesn't exist
        Optional<DBContact> existingContact = contactRepo.findByEmail(creationContact.getEmail());
        if (existingContact.isPresent()) {
            return Optional.<DBContact>empty();
        }

        // Make new contact
        DBContact newContact = new DBContact();
        newContact.setEmail(creationContact.getEmail());
        newContact.setFirstName(creationContact.getFirstName());
        newContact.setLastName(creationContact.getLastName());
        newContact.setFullName(creationContact.getLastName().toUpperCase() + " " + creationContact.getFirstName());
        newContact.setAddress(creationContact.getAddress());
        newContact.setMobileNumber(creationContact.getMobileNumber());

        // Save and return
        return Optional.of((contactRepo.save(newContact)));
    }
}
