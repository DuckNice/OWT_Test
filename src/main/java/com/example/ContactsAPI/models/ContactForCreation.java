package com.example.ContactsAPI.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactForCreation {
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @NotBlank(message = "Address is mandatory")
    private String address;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;
    // @Patter
    private String mobileNumber;
}
