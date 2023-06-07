package com.example.ContactsAPI.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.models.contact.ContactForCreation;
import com.example.ContactsAPI.models.contact.DBContact;
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
    public ResponseEntity<DBContact> createOne(@RequestBody @Valid ContactForCreation creationContact,
            HttpServletRequest req) {
        Optional<DBContact> newContact = contactService.createWUnknownId(creationContact);

        // Created or conflict
        if (newContact.isPresent()) {
            Long newContactId = newContact.get().getId();
            String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

            return ResponseEntity.created(URI.create(url + "/api/contacts/" +
                    newContactId)).body(newContact.get());
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DBContact> findOne(@PathVariable Long id) {
        Optional<DBContact> entry = contactRepo.findById(id);

        if (entry.isPresent()) {
            return ResponseEntity.ok(entry.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DBContact> updateOne(@PathVariable Long id,
            @RequestBody @Valid ContactForCreation creationContact,
            HttpServletRequest req) {
        // Perform save
        Pair<Boolean, Optional<DBContact>> operationResponse = contactService.createOrUpdateWKnownId(id,
                creationContact);
        Optional<DBContact> newContact = operationResponse.getSecond();

        // Conflict
        if (newContact.isEmpty()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }

        // Updated or created
        if (operationResponse.getFirst()) {
            return ResponseEntity.ok(newContact.get());
        } else {
            String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

            return ResponseEntity.created(URI.create(url + "/api/contacts/" +
                    id)).body(newContact.get());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOne(@PathVariable Long id) {
        Boolean entryExists = contactRepo.existsById(id);
        contactRepo.deleteById(id);

        // Was deleted
        return entryExists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
