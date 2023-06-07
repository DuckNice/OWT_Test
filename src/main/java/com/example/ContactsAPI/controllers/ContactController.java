package com.example.ContactsAPI.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.models.ContactForCreation;
import com.example.ContactsAPI.models.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;
import com.example.ContactsAPI.services.ContactCrudService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("api/contacts")
public class ContactController {
    @Autowired
    ContactRepository contactRepo;
    @Autowired
    ContactCrudService contactService;

    @GetMapping
    public ResponseEntity<List<DBContact>> findAll() {
        List<DBContact> contacts = IterableUtils.toList(contactRepo.findAll());
        if (contacts.size() == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    @PutMapping
    public ResponseEntity<DBContact> createOne(@RequestBody @Valid ContactForCreation creationContact,
            HttpServletRequest req) {
        Optional<DBContact> newContact = contactService.createWUnknownId(creationContact);

        // Make new contact
        String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

        // Set or update
        if (newContact.isPresent()) {
            Long newContactId = newContact.get().getId();

            return ResponseEntity.created(URI.create(url + "/api/contacts/" +
                    newContactId)).body(newContact.get());
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }
    }
}
