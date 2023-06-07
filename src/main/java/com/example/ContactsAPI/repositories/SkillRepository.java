package com.example.ContactsAPI.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ContactsAPI.models.skill.DBSkill;

@Repository
public interface SkillRepository extends CrudRepository<DBSkill, Long> {
    Optional<DBSkill> findByName(String name);
}
