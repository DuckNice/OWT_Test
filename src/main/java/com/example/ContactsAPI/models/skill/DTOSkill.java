package com.example.ContactsAPI.models.skill;

import com.example.ContactsAPI.models.DTObject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOSkill implements DTObject {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Skill is mandatory")
    private SkillLevel skill;
}
