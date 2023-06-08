package com.example.ContactsAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.ContactsAPI.models.DBObject;
import com.example.ContactsAPI.models.DTObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

abstract class BaseEndpointTests<DB extends DBObject, DT extends DTObject, R extends CrudRepository<DB, Long>> {
    protected final String API_ROOT;

    protected BaseEndpointTests(String API_ROOT) {
        this.API_ROOT = API_ROOT;
    }

    @Autowired
    protected R repo;

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setPort() {
        RestAssured.port = this.port;
        repo.deleteAll();
    }

    protected abstract DT createRandomObject();

    protected abstract DT createInvalidObject();

    protected String getEntryUrl(Response response) {
        return response.getHeader("location");
    }

    protected String getRandomEntryUrl() {
        return API_ROOT + "/" + RandomStringUtils.randomNumeric(10);
    }

    // C
    @Test
    void createValid_singleEntry_then201() {
        // Given
        DT entry = createRandomObject();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry)
                .post(API_ROOT);

        // Then
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCode());
    }

    @Test
    void createInvalid_entry_then400() {
        // Given
        DT entry = createInvalidObject();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).post(API_ROOT);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), createResponse.getStatusCode());
    }

    // R
    @Test
    void readFound_singleEntry_then200() {
        // Given
        DT entry = createRandomObject();
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).post(API_ROOT);

        // When
        Response getResponse = RestAssured.get(getEntryUrl(createResponse));

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
    }

    @Test
    void readMissing_singleEntry_then404() {
        // Given

        // When
        Response getResponse = RestAssured.get(getRandomEntryUrl());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getStatusCode());
    }

    @Test
    void readFound_entryList_then200() {
        // Given
        DT entry = createRandomObject();
        DT entry2 = createRandomObject();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).post(API_ROOT);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry2).post(API_ROOT);

        // When
        Response getResponse = RestAssured.get(API_ROOT);

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        assertEquals(2, getResponse.body().as(Object[].class).length);
    }

    @Test
    void readNone_entryList_then204() {
        // Given

        // When
        Response getResponse = RestAssured.get(API_ROOT);

        // Then
        assertEquals(HttpStatus.NO_CONTENT.value(), getResponse.getStatusCode());
    }

    // U
    @Test
    void updateWithValid_singleEntry_then200() {
        // Given
        DT entry = createRandomObject();
        DT entry2 = createRandomObject();

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).post(API_ROOT);
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry2).put(getEntryUrl(createResponse));

        // Then
        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());
    }

    @Test
    void updateMissing_singleEntry_then201() {
        // Given
        DT entry = createRandomObject();

        // When
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).put(getRandomEntryUrl());

        // Then
        assertEquals(HttpStatus.CREATED.value(), updateResponse.getStatusCode());
    }

    @Test
    void UpdateWithInvalid_singleEntry_then400() {
        // Given
        DT entry = createInvalidObject();

        // When
        Response updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).put(getRandomEntryUrl());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse.getStatusCode());
    }

    // D
    @Test
    void deleteExisting_singleEntry_then200() {
        // Given
        DT entry = createRandomObject();
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(entry).post(API_ROOT);

        String entryLoc = getEntryUrl(createResponse);

        // When
        Response deleteResponse = RestAssured.delete(entryLoc);

        // Then
        assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode());
        Response getResponse = RestAssured.get(entryLoc);
        assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getStatusCode());
    }

    @Test
    void deleteMissing_singleEntry_then404() {
        // Given

        // When
        Response deleteResponse = RestAssured.delete(getRandomEntryUrl());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getStatusCode());
    }
}
