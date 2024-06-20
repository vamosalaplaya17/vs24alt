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
import org.thws.management.client.UniModuleClient;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.UniModule;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Integration tests for the UniModule part of the backend
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UniModuleApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private PartnerUniversityClient partnerUniversityClient;
    private UniModuleClient uniModuleClient;

    //sets up new clients before each test is run
    @BeforeEach
    public void setUp() {
        uniModuleClient = new UniModuleClient(testRestTemplate.getRestTemplate());
        partnerUniversityClient = new PartnerUniversityClient(testRestTemplate.getRestTemplate());
    }

    //resets the database to its initial state after each test is run
    @AfterEach
    void tearDown() {
        uniModuleClient.resetDatabase();
    }

    /**
     * Tests adding new UniModules to PartnerUniversities.
     * Expected: status code 201 after successful creation, 409 when module with name already exists,
     * 404 when university to be added to is not found
     */
    @Test
    void testAddUniModuleToPartnerUniversity() {
        PartnerUniversity partnerUniversity = partnerUniversityClient.getSinglePartnerUniversity(1L).getBody();

        UniModule uniModule1 = new UniModule(
                "module 1 name",
                1,
                1,
                partnerUniversity
        );

        UniModule uniModule2 = new UniModule(
                "module 2 name",
                1,
                1,
                partnerUniversity
        );

        ResponseEntity<UniModule> response = uniModuleClient.addNewUniModuleToPartnerUniversity(1L, uniModule1);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(HttpStatus.CONFLICT, uniModuleClient.addNewUniModuleToPartnerUniversity(1L, uniModule1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.addNewUniModuleToPartnerUniversity(30L, uniModule2).getStatusCode());
    }

    /**
     * Tests fetching a single uni module from the database.
     * Expected: status code 200 when successful, 404 when either university or module is not found
     */
    @Test
    void testGetSingleUniModule() {
        assertEquals(HttpStatus.OK, uniModuleClient.getSingleUniModule(1L, 1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getSingleUniModule(1L, 99L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getSingleUniModule(99L, 1L).getStatusCode());
    }

    /**
     * Fetches all UniModules from the database.
     * Expected: status code 200 when successfully fetching something, with initial setup exactly 2 modules in university 1
     * 404 when fetching nothing
     */
    @Test
    void getAllUniModulesFromPartnerUniversity() {
        ResponseEntity<PagedModel<UniModule>> response = uniModuleClient.getAllUniModulesFromPartnerUniversity(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());

        uniModuleClient.deleteUniModule(1L, 1L);
        uniModuleClient.deleteUniModule(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getAllUniModulesFromPartnerUniversity(1L).getStatusCode());
    }

    /**
     * Tests updating uni module, same approach as with the PartnerUniversity test
     * Expected: status code 200 when successfully updating, 404 works correctly in e.g. Postman, hard to reproduce here
     */
    @Test
    void updateUniModule() {
        ResponseEntity<UniModule> response1 = uniModuleClient.getSingleUniModule(1L, 1L);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        UniModule oldModel = response1.getBody();
        String oldName = oldModel.getName();

        oldModel.setName("new name");
        assertEquals(HttpStatus.OK, uniModuleClient.updateUniModule(1L, oldModel).getStatusCode());

        ResponseEntity<UniModule> response2 = uniModuleClient.getSingleUniModule(1L, 1L);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UniModule updatedModel = response2.getBody();
        String updatedName = updatedModel.getName();

        assertNotEquals(oldName, updatedName);
    }

    /**
     * Tests deleting UniModules.
     * Expected: status code 204 upon successful deletion, 404 when module to be deleted is not found
     */
    @Test
    void deleteUniModule() {
        assertEquals(HttpStatus.NO_CONTENT, uniModuleClient.deleteUniModule(1L, 1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.deleteUniModule(1L, 1L).getStatusCode());
    }
}
