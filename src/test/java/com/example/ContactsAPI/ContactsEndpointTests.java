package com.example.ContactsAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.ContactsAPI.models.ContactForCreation;
import com.example.ContactsAPI.models.DBContact;
import com.example.ContactsAPI.repositories.ContactRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactsEndpointTests {
    private static final String API_ROOT = "http://localhost/api/contacts";

    @Autowired
    ContactRepository contactRepo;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setPort() {
        RestAssured.port = this.port;
        contactRepo.deleteAll();
    }

    private ContactForCreation createRandomContact() {
        ContactForCreation contact = new ContactForCreation();
        contact.setFirstName(RandomStringUtils.randomAlphabetic(10));
        contact.setLastName(RandomStringUtils.randomAlphabetic(10));
        contact.setAddress(RandomStringUtils.randomAlphabetic(10));
        contact.setEmail(RandomStringUtils.randomAlphabetic(10) + "@hotmail.com");
        contact.setMobileNumber("+41 12 345 67 89");
        return contact;
    }

    private String getContactUrl(Response response) {
        return response.getHeader("location");
    }

    private String getRandomContactUrl() {
        return API_ROOT + "/" + RandomStringUtils.randomNumeric(10);
    }

    // C
    @Test
    void createValid_contact_then201() {
        // Given
        ContactForCreation contact = createRandomContact();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact)
                .post(API_ROOT);

        // Then
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCode());
    }

    @Test
    void createExistingEmail_contact_then409Conflict() {
        // Given
        ContactForCreation contact = createRandomContact();

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

    @Test
    void createInvalid_contact_then400() {
        // Given
        ContactForCreation contact = createRandomContact();
        contact.setEmail(RandomStringUtils.randomAlphabetic(10));

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), createResponse.getStatusCode());
    }

    // R
    @Test
    void readFound_singleContact_then200() {
        // Given
        ContactForCreation contact = createRandomContact();
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // When
        Response getResponse = RestAssured.get(getContactUrl(createResponse));

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        assertEquals(contact.getEmail(), getResponse.body().as(DBContact.class).getEmail());
    }

    @Test
    void readMissing_singleContact_then404() {
        // Given

        // When
        Response getResponse = RestAssured.get(getRandomContactUrl());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getStatusCode());
    }

    @Test
    void readFound_contactList_then200() {
        // Given
        ContactForCreation contact = createRandomContact();
        ContactForCreation contact2 = createRandomContact();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).post(API_ROOT);

        // When
        Response getResponse = RestAssured.get(API_ROOT);

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        assertEquals(2, getResponse.body().as(DBContact[].class).length);
    }

    @Test
    void readNone_contactList_then204() {
        // Given

        // When
        Response getResponse = RestAssured.get(API_ROOT);

        // Then
        assertEquals(HttpStatus.NO_CONTENT.value(), getResponse.getStatusCode());
    }

    // U
    @Test
    void updateWithValid_contact_then200() {
        // Given
        ContactForCreation contact = createRandomContact();
        ContactForCreation contact2 = createRandomContact();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).put(getContactUrl(createResponse));

        // Then
        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());
        assertEquals(contact2.getEmail(), updateResponse.body().as(DBContact.class).getEmail());
    }

    @Test
    void updateMissing_contact_then201() {
        // Given
        ContactForCreation contact = createRandomContact();

        // When
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).put(getRandomContactUrl());

        // Then
        assertEquals(HttpStatus.CREATED.value(), updateResponse.getStatusCode());
    }

    @Test
    void UpdateWithInvalid_contact_then400() {
        // Given
        ContactForCreation contact = createRandomContact();
        contact.setEmail(RandomStringUtils.randomAlphabetic(10));

        // When
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).put(getRandomContactUrl());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse.getStatusCode());
    }

    @Test
    void updateWithExisting_email_then409() {
        // Given
        ContactForCreation contact = createRandomContact();
        ContactForCreation otherContact = createRandomContact();

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
                .body(contact).put(getContactUrl(createResponse));

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), updateResponse.getStatusCode());
    }

    // D
    @Test
    void deleteExisting_contact_then200() {
        // Given
        ContactForCreation contact = createRandomContact();
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        String contactLoc = getContactUrl(createResponse);

        // When
        Response deleteResponse = RestAssured.delete(contactLoc);

        // Then
        assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode());
        Response getResponse = RestAssured.get(contactLoc);
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getStatusCode());
    }

    @Test
    void deleteMissing_contact_then404() {
        // Given

        // When
        Response deleteResponse = RestAssured.delete(getRandomContactUrl());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getStatusCode());
    }
}
