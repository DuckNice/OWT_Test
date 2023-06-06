package com.example.ContactsAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Contact {
    @Id
    private long id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String Address;
    private String email;
    private String mobileNumber;
}
