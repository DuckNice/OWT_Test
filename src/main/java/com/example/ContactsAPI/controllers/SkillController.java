package com.example.ContactsAPI.controllers;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.DTOSkill;
import com.example.ContactsAPI.repositories.SkillRepository;
import com.example.ContactsAPI.services.SkillCrudService;

import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("api/skills")
public class SkillController
        extends BaseRestController<DBSkill, DTOSkill, SkillRepository, SkillCrudService> {

    @Override
    protected String generateObjectUrl(DBSkill contact, HttpServletRequest req) {
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

        return baseUrl + "/api/skills/" + contact.getId();
    }

    @Override
    protected DBSkill prepareObjectForReturn(DBSkill entry) {
        if (entry.getContacts() == null) {
            return entry;
        }

        Stream<DBContact> contacts = entry.getContacts().stream().map((DBContact contact) -> {
            contact.setSkills(new HashSet<DBSkill>());
            return contact;
        });

        entry.setContacts(contacts.collect(Collectors.toSet()));

        return entry;
    }
}
