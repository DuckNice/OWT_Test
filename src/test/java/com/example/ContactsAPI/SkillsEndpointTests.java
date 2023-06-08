package com.example.ContactsAPI;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.DTOSkill;
import com.example.ContactsAPI.models.skill.SkillLevel;
import com.example.ContactsAPI.repositories.SkillRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SkillsEndpointTests extends BaseEndpointTests<DBSkill, DTOSkill, SkillRepository> {
    public SkillsEndpointTests() {
        super("http://localhost/api/skills");
    }

    @Override
    protected DTOSkill createRandomObject() {
        DTOSkill skill = new DTOSkill();
        skill.setName(RandomStringUtils.randomAlphabetic(10));
        skill.setSkill(SkillLevel.JOURNEYMAN);
        return skill;
    }

    @Override
    protected DTOSkill createInvalidObject() {
        DTOSkill skill = createRandomObject();
        skill.setName("");

        return skill;
    }
}
