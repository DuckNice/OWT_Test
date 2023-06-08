package com.example.ContactsAPI.models.skill;

import java.util.Set;

import com.example.ContactsAPI.models.DBObject;
import com.example.ContactsAPI.models.contact.DBContact;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueNameAndLevel", columnNames = { "NAME", "LEVEL" }) })
public class DBSkill implements DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, unique = true)
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "LEVEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillLevel level;
    @Column(name = "CONTACTS")
    @ManyToMany(mappedBy = "skills")
    private Set<DBContact> contacts;
}
