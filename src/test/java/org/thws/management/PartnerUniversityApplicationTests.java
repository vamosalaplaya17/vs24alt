package org.thws.management;

import org.junit.jupiter.api.*;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartnerUniversityApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private PartnerUniversityClient partnerUniversityClient;

    @BeforeEach
    void setUp() {
        partnerUniversityClient = new PartnerUniversityClient(testRestTemplate.getRestTemplate());
    }

    @Test
    void testGetSinglePartnerUniversity() {
        assertEquals(HttpStatus.OK, partnerUniversityClient.getSinglePartnerUniversity(1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.getSinglePartnerUniversity(99).getStatusCode());
    }

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

    @Test
    void updatePartnerUniversity() {
        ResponseEntity<PartnerUniversity> response1 = partnerUniversityClient.getSinglePartnerUniversity(2);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        PartnerUniversity oldModel = response1.getBody();
        String oldName = oldModel.getName();

        oldModel.setName("new name");
        partnerUniversityClient.updatePartnerUniversity(oldModel);
        ResponseEntity<PartnerUniversity> response2 = partnerUniversityClient.getSinglePartnerUniversity(2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        PartnerUniversity updatedModel = response2.getBody();
        String updatedName = updatedModel.getName();

        assertNotEquals(oldName, updatedName);

        ResponseEntity<PartnerUniversity> response3 = partnerUniversityClient.getSinglePartnerUniversity(2);
        PartnerUniversity deletedModel = response3.getBody();
        deletedModel.setName("deleted name");
        partnerUniversityClient.deletePartnerUniversity(2);
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.updatePartnerUniversity(deletedModel).getStatusCode());
    }

    @Test
    void deletePartnerUniversity() {
        assertEquals(HttpStatus.NO_CONTENT, partnerUniversityClient.deletePartnerUniversity(1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, partnerUniversityClient.deletePartnerUniversity(1).getStatusCode());
    }

    @Test
    void testGetAllPartnerUniversities() {
        ResponseEntity<PagedModel<PartnerUniversity>> response = partnerUniversityClient.getAllPartnerUniversities();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        partnerUniversityClient.deletePartnerUniversity(1);
        partnerUniversityClient.deletePartnerUniversity(2);
        ResponseEntity<PagedModel<PartnerUniversity>> response2 = partnerUniversityClient.getAllPartnerUniversities();
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

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
}
