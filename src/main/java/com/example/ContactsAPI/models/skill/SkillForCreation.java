package com.example.ContactsAPI.models.skill;

import com.example.ContactsAPI.models.DTObject;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillForCreation implements DTObject {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Skill is mandatory")
    private SkillLevel skill;
}
