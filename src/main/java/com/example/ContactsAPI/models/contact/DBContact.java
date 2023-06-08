package com.example.ContactsAPI.models.contact;

import java.util.HashSet;
import java.util.Set;

import com.example.ContactsAPI.models.skill.DBSkill;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DBContact {
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CONTACT_SKILLS", joinColumns = @JoinColumn(name = "CONTACT_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "SKILL_ID", referencedColumnName = "ID"))
    private Set<DBSkill> skills = new HashSet<>();
}
