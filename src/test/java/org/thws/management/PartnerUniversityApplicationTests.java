package org.thws.management;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.thws.management.client.PartnerUniversityClient;
import org.thws.management.server.model.PartnerUniversity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Integration tests for the PartnerUniversity part of the backend
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartnerUniversityApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private PartnerUniversityClient partnerUniversityClient;

    //sets up a new client before a test is run
    @BeforeEach
    void setUp() {
        partnerUniversityClient = new PartnerUniversityClient(testRestTemplate.getRestTemplate());
    }

    //resets the database to initial state after a test is run
    @AfterEach
    void tearDown() {
        partnerUniversityClient.resetDatabase();
    }

    /**
     * Tests adding partner universities.
     * Expected: status code 201 for creation, 409 when name already exists
     */
    @Test
    void addPartnerUniversity() {
        PartnerUniversity partnerUniversity1 = new PartnerUniversity(
                "test university 1",
                "test country 1",
                "test department name 1",
                "test department url 1",
                "test contact person 1",
                1,
                1,
                LocalDate.of(2024, 5, 20),
                LocalDate.of(2024, 5, 20).plusMonths(1)
        );

        assertEquals(HttpStatus.CREATED, partnerUniversityClient.addNewPartnerUniversity(partnerUniversity1).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, partnerUniversityClient.addNewPartnerUniversity(partnerUniversity1).getStatusCode());
    }

    /**
     * Tests fetching a single partner university from the database.
     * Expected: status code 200 when university exists, 404 when it doesn't exist
     */
    @Test
    void testGetSinglePartnerUniversity() {
        assertEquals(HttpStatus.OK, partnerUniversityClient.getSinglePartnerUniversity(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.getSinglePartnerUniversity(99L).getStatusCode());
    }

    /**
     * Tests fetching all universities from the database.
     * Expected: status code 200 when fetching something, 404 when fetching nothing
     */
    @Test
    void testGetAllPartnerUniversities() {
        ResponseEntity<PagedModel<PartnerUniversity>> response = partnerUniversityClient.getAllPartnerUniversities();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().getContent().size());

        assertEquals(HttpStatus.NO_CONTENT, partnerUniversityClient.deletePartnerUniversity(1L).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, partnerUniversityClient.deletePartnerUniversity(2L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.getAllPartnerUniversities().getStatusCode());
    }

    /**
     * Tests fetching all universities from the database, with different types of filters.
     * Expected: status code 200 when fetching something, 404 when fetching nothing
     */
    @Test
    void testGetPartnerUniversityByFilters() {
        PartnerUniversity filter1 = new PartnerUniversity(
                "filter1 university",
                "test country",
                "filter3 department name",
                "test department url",
                "test contact person",
                1,
                1,
                LocalDate.of(2024, 5, 20),
                LocalDate.of(2024, 5, 20).plusMonths(1)
        );

        PartnerUniversity filter2 = new PartnerUniversity(
                "filter2 university",
                "test country",
                "filter2 department name",
                "test department url",
                "test contact person",
                1,
                1,
                LocalDate.of(2024, 5, 20),
                LocalDate.of(2024, 5, 20).plusMonths(1)
        );

        PartnerUniversity filter3 = new PartnerUniversity(
                "filter3 university",
                "test country",
                "filter3 department name",
                "test department url",
                "test contact person",
                1,
                1,
                LocalDate.of(2024, 5, 20),
                LocalDate.of(2024, 5, 20).plusMonths(1)
        );

        partnerUniversityClient.addNewPartnerUniversity(filter1);
        partnerUniversityClient.addNewPartnerUniversity(filter2);
        partnerUniversityClient.addNewPartnerUniversity(filter3);

        ResponseEntity<PagedModel<PartnerUniversity>> response1 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                "filter1 university", null, null);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        PagedModel<PartnerUniversity> universities1 = response1.getBody();
        assertEquals(1, universities1.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response2 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                null, "test country", null);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        PagedModel<PartnerUniversity> universities2 = response2.getBody();
        assertEquals(3, universities2.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response3 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                null, null, "filter3 department name");
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        PagedModel<PartnerUniversity> universities3 = response3.getBody();
        assertEquals(2, universities3.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response4 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                "filter1 university", "test country", null);
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        PagedModel<PartnerUniversity> universities4 = response4.getBody();
        assertEquals(1, universities4.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response5 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                "filter2 university", null, "filter2 department name");
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        PagedModel<PartnerUniversity> universities5 = response5.getBody();
        assertEquals(1, universities5.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response6 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                null, "test country", "filter3 department name");
        assertEquals(HttpStatus.OK, response6.getStatusCode());

        PagedModel<PartnerUniversity> universities6 = response6.getBody();
        assertEquals(2, universities6.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response7 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                "filter3 university", "test country", "filter3 department name");
        assertEquals(HttpStatus.OK, response7.getStatusCode());

        PagedModel<PartnerUniversity> universities7 = response7.getBody();
        assertEquals(1, universities7.getContent().size());


        ResponseEntity<PagedModel<PartnerUniversity>> response8 = partnerUniversityClient.getAllPartnerUniversitiesByFilters(
                "non existent", "non existent", "non existent");
        assertEquals(HttpStatus.NOT_FOUND, response8.getStatusCode());
    }

    /**
     * Tests updating values of partner universities.
     * Expected: status code 200 when successfully updating, 404 works correctly in e.g. Postman, hard to reproduce here
     */
    @Test
    void updatePartnerUniversity() {
        //fetches university
        ResponseEntity<PartnerUniversity> response1 = partnerUniversityClient.getSinglePartnerUniversity(2L);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        //saves its name in a String
        PartnerUniversity oldModel = response1.getBody();
        String oldName = oldModel.getName();

        //sets a new name and tries to update
        oldModel.setName("new name");
        assertEquals(HttpStatus.OK, partnerUniversityClient.updatePartnerUniversity(oldModel).getStatusCode());

        //fetches same university again
        ResponseEntity<PartnerUniversity> response2 = partnerUniversityClient.getSinglePartnerUniversity(2L);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        //saves its name again
        PartnerUniversity updatedModel = response2.getBody();
        String updatedName = updatedModel.getName();

        //compares if names are not equal
        assertNotEquals(oldName, updatedName);
    }

    /**
     * Tests deleting partner universities.
     * Expected: status code 204 upon successful deletion, 404 when university is not found
     */
    @Test
    void deletePartnerUniversity() {
        assertEquals(HttpStatus.NO_CONTENT, partnerUniversityClient.deletePartnerUniversity(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.deletePartnerUniversity(1L).getStatusCode());
    }
}
