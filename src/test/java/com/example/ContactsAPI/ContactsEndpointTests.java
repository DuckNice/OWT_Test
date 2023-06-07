package com.example.ContactsAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.ContactsAPI.models.contact.ContactForCreation;
import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactsEndpointTests extends BaseEndpointTests<DBContact, ContactForCreation, ContactRepository> {
    public ContactsEndpointTests() {
        super("http://localhost/api/contacts");
    }

    @Override
    protected ContactForCreation createRandomObject() {
        ContactForCreation contact = new ContactForCreation();
        contact.setFirstName(RandomStringUtils.randomAlphabetic(10));
        contact.setLastName(RandomStringUtils.randomAlphabetic(10));
        contact.setAddress(RandomStringUtils.randomAlphabetic(10));
        contact.setEmail(RandomStringUtils.randomAlphabetic(10) + "@hotmail.com");
        contact.setMobileNumber("+41 12 345 67 89");
        return contact;
    }

    @Override
    protected ContactForCreation createInvalidObject() {
        ContactForCreation contact = createRandomObject();
        contact.setEmail(RandomStringUtils.randomAlphabetic(10));

        return contact;
    }

    // C

    @Test
    void createExisting_email_then409Conflict() {
        // Given
        ContactForCreation contact = createRandomObject();

        // When
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), createResponse.getStatusCode());
    }

    // U

    @Test
    void updateWithExisting_email_then409() {
        // Given
        ContactForCreation contact = createRandomObject();
        ContactForCreation otherContact = createRandomObject();

        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // When
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(otherContact).post(API_ROOT);

        contact.setEmail(otherContact.getEmail());

        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).put(getEntryUrl(createResponse));

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), updateResponse.getStatusCode());
    }
}
