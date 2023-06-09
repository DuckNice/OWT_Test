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

import com.example.ContactsAPI.models.contact.DBContact;
import com.example.ContactsAPI.models.contact.DTOContact;
import com.example.ContactsAPI.models.skill.DBSkill;
import com.example.ContactsAPI.models.skill.DTOSkill;
import com.example.ContactsAPI.models.skill.SkillLevel;
import com.example.ContactsAPI.repositories.ContactRepository;
import com.example.ContactsAPI.repositories.SkillRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RelationsEndpointTests {
    public final static String CONTACT_API_ROOT = "http://localhost/api/contacts";
    public final static String SKILL_API_ROOT = "http://localhost/api/skills";

    @Autowired
    protected ContactRepository contactRepo;

    @Autowired
    protected SkillRepository skillRepo;

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setPort() {
        RestAssured.port = this.port;
        contactRepo.deleteAll();
        skillRepo.deleteAll();
    }

    private DTOContact createRandomContact() {
        DTOContact contact = new DTOContact();
        contact.setFirstName(RandomStringUtils.randomAlphabetic(10));
        contact.setLastName(RandomStringUtils.randomAlphabetic(10));
        contact.setAddress(RandomStringUtils.randomAlphabetic(10));
        contact.setEmail(RandomStringUtils.randomAlphabetic(10) + "@hotmail.com");
        contact.setMobileNumber("+41 12 345 67 89");
        return contact;
    }

    private DTOSkill createRandomSkill() {
        DTOSkill skill = new DTOSkill();
        skill.setName(RandomStringUtils.randomAlphabetic(10));
        skill.setSkill(SkillLevel.JOURNEYMAN);
        return skill;
    }

    protected String getEntryUrl(Response response) {
        return response.getHeader("location");
    }

    // C

    @Test
    void onCreate_ContactWSkill_ThenExists() {
        // Given
        DTOContact contact = createRandomContact();
        contact.setSkills(new DTOSkill[] { createRandomSkill(), createRandomSkill() });

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(CONTACT_API_ROOT);

        // Then
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCode());
        assertEquals(2, skillRepo.count());
        assertEquals(1, contactRepo.count());
    }

    @Test
    void onCreate_SeveralContactWSkill_PreserveSkill() {
        // Given
        DTOSkill persistedSkill = createRandomSkill();
        DTOContact contact = createRandomContact();
        contact.setSkills(new DTOSkill[] { persistedSkill, createRandomSkill() });
        DTOContact contact2 = createRandomContact();
        contact2.setSkills(new DTOSkill[] { persistedSkill, createRandomSkill() });

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(CONTACT_API_ROOT);

        Response createResponse2 = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact2).post(CONTACT_API_ROOT);
        // Then
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCode());
        assertEquals(HttpStatus.CREATED.value(), createResponse2.getStatusCode());
        assertEquals(3, skillRepo.count());
        assertEquals(2, contactRepo.count());
    }

    // R

    @Test
    void onRead_ContactWSkill_ThenExists() {
        // Given
        DTOContact contact = createRandomContact();
        contact.setSkills(new DTOSkill[] { createRandomSkill(), createRandomSkill() });

        // When
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(CONTACT_API_ROOT);

        // Then
        Response getResponse = RestAssured.get(getEntryUrl(createResponse));
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        System.out.println(getResponse.getBody().asPrettyString());
        assertEquals(2, getResponse.then().assertThat().extract().as(DBContact.class)
                .getSkills().size());
    }

    @Test
    void onRead_ContactWSkill_ThenSkillHasContact() {
        // Given
        DTOContact contact = createRandomContact();
        contact.setSkills(new DTOSkill[] { createRandomSkill(), createRandomSkill() });
        Response createResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contact).post(CONTACT_API_ROOT);
        DBSkill skill = createResponse.getBody().as(DBContact.class).getSkills().stream().toList().get(0);

        // When

        Response getResponse = RestAssured.get(SKILL_API_ROOT + "/" + skill.getId());

        // Then
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode());
        System.out.println(getResponse.getBody().asPrettyString());
        assertEquals(1, getResponse.then().assertThat().extract().as(DBSkill.class)
                .getContacts().size());
    }
}
