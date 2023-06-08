package com.example.ContactsAPI.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.SkillLevel;

@Repository
public interface SkillRepository extends CrudRepository<DBSkill, Long> {
    Optional<DBSkill> findByNameAndLevel(String name, SkillLevel level);

    Optional<Set<DBSkill>> findAllByNameAndLevel(String name, SkillLevel level);
}
