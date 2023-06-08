package com.example.ContactsAPI.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
