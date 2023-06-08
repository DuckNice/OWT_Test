package com.example.ContactsAPI.models.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOSkill {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Skill is mandatory")
    private SkillLevel skill;
}
