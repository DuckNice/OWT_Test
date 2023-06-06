package com.example.ContactsAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.ContactsAPI.models.Contact;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactsEndpointTests {
    private static final String API_ROOT = "http://localhost/api/contacts";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setPort() {
        RestAssured.port = this.port;
    }

    private Contact createRandomContact() {
        Contact contact = new Contact();
        contact.setFirstName(RandomStringUtils.randomAlphabetic(10));
        contact.setLastName(RandomStringUtils.randomAlphabetic(10));
        contact.setAddress(RandomStringUtils.randomAlphabetic(10));
        contact.setEmail(RandomStringUtils.randomAlphabetic(10) + "@hotmail.com");
        contact.setMobileNumber("+41 12 345 67 89");
        return contact;
    }

    private String getContactUrl(Contact contact) {
        return API_ROOT + "/" + contact.getId();
    }

    // C
    @Test
    void createValid_contact_then201() {
        // Given
        Contact contact = createRandomContact();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact)
                .post(API_ROOT);

        // Then
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCode());
    }

    @Test
    void createExisting_contact_then200Updated() {
        // Given
        Contact contact = createRandomContact();
        Contact contact2 = createRandomContact();

        // When
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).post(API_ROOT);

        // Then
        assertEquals(HttpStatus.OK.value(), createResponse.getStatusCode());
        assertEquals(contact2, createResponse.body().as(Contact.class));
    }

    @Test
    void createInvalid_contact_then400() {
        // Given
        Contact contact = createRandomContact();
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
        Contact contact = createRandomContact();
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // When
        Response getResponse = RestAssured.get(getContactUrl(createResponse.body().as(Contact.class)));

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        assertEquals(contact.getFirstName(), getResponse.body().as(Contact.class).getFirstName());
    }

    @Test
    void readMissing_singleContact_then404() {
        // Given
        Contact contact = createRandomContact();

        // When
        Response getResponse = RestAssured.get(getContactUrl(contact));

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getStatusCode());
    }

    @Test
    void readFound_contactList_then200() {
        // Given
        Contact contact = createRandomContact();
        Contact contact2 = createRandomContact();
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
        assertEquals(2, getResponse.body().as(Contact[].class).length);
    }

    @Test
    void readNone_contactList_then204() {
        // Given

        // When
        Response getResponse = RestAssured.get(API_ROOT);

        // Then
        assertEquals(HttpStatus.NO_CONTENT.value(), getResponse.getStatusCode());
        assertEquals(0, getResponse.body().as(Contact[].class).length);
    }

    // U
    @Test
    void updateWithValid_contact_then200() {
        // Given
        Contact contact = createRandomContact();
        Contact contact2 = createRandomContact();

        // When
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).patch(getContactUrl(contact));

        // Then
        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());
        assertEquals(contact2, updateResponse.body().as(Contact.class));
    }

    @Test
    void updateMissing_contact_then201() {
        // Given
        Contact contact = createRandomContact();

        // When
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).patch(getContactUrl(contact));

        // Then
        assertEquals(HttpStatus.CREATED.value(), updateResponse.getStatusCode());
    }

    @Test
    void UpdateWithInvalid_contact_then400() {
        // Given
        Contact contact = createRandomContact();
        Contact contact2 = createRandomContact();
        contact2.setEmail(RandomStringUtils.randomAlphabetic(10));

        // When
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).patch(getContactUrl(contact));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse.getStatusCode());
    }

    // D
    @Test
    void deleteExisting_contact_then200() {
        // Given
        Contact contact = createRandomContact();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(API_ROOT);

        // When
        Response deleteResponse = RestAssured.delete(getContactUrl(contact));

        // Then
        assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode());
        Response getResponse = RestAssured.get(getContactUrl(contact));
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse);
    }

    @Test
    void deleteMissing_contact_then404() {
        // Given

        // When
        Response deleteResponse = RestAssured.delete(API_ROOT + "/" + RandomStringUtils.randomAlphanumeric(5));

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getStatusCode());
    }
}
