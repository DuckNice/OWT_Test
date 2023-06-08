package com.example.ContactsAPI.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.models.contact.DTOContact;
import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;
import com.example.ContactsAPI.services.ContactCrudService;

import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("api/contacts")
public class ContactController
        extends BaseRestController<DBContact, DTOContact, ContactRepository, ContactCrudService> {

    @Override
    protected String generateObjectUrl(DBContact contact, HttpServletRequest req) {
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

        return baseUrl + "/api/contacts/" + contact.getId();
    }
}
