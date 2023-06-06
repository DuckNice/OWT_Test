package com.example.ContactsAPI.controllers;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.models.Contact;
import com.example.ContactsAPI.repositories.ContactRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("api/contacts")
public class ContactController {
    @Autowired
    ContactRepository contactRepo;

    @GetMapping
    public List<Contact> findAll(HttpServletResponse response) {
        List<Contact> contacts = IterableUtils.toList(contactRepo.findAll());
        if (contacts.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }

        return contacts;
    }
}
