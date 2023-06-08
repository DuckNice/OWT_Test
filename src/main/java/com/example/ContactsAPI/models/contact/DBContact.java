package com.example.ContactsAPI.models.contact;

import java.util.Set;

import com.example.ContactsAPI.models.DBObject;
import com.example.ContactsAPI.models.skill.DBSkill;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBContact implements DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, unique = true)
    private long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;
    @Column(name = "ADDRESS", nullable = false)
    private String address;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    @Column(name = "SKILLS")
    @ManyToMany
    @JoinTable(name = "CONTACT_SKILLS", joinColumns = @JoinColumn(name = "CONTACT_ID"), inverseJoinColumns = @JoinColumn(name = "SKILL_ID"))
    private Set<DBSkill> skills;
}
