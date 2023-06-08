package com.example.ContactsAPI.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.SkillForCreation;
import com.example.ContactsAPI.repositories.SkillRepository;

@Service
public class SkillCrudService extends CrudService<DBSkill, SkillForCreation, SkillRepository> {

    @Override
    protected DBSkill dbEntryFromDTO(SkillForCreation dto) {
        DBSkill newSkill = new DBSkill();
        newSkill.setName(dto.getName());
        newSkill.setLevel(dto.getSkill());

        return newSkill;
    }

    @Override
    protected boolean dbConflictValidation(SkillForCreation dto, Long id) {
        Optional<DBSkill> existingNamedSkill = repo.findByName(dto.getName());

        return existingNamedSkill.isEmpty() || existingNamedSkill.get().getId() == id;
    }
}
