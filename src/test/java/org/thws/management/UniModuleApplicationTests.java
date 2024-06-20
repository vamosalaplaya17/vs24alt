package org.thws.management;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UniModuleApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private PartnerUniversityClient partnerUniversityClient;
    private UniModuleClient uniModuleClient;

    @BeforeEach
    public void setUp() {
        uniModuleClient = new UniModuleClient(testRestTemplate.getRestTemplate());
        partnerUniversityClient = new PartnerUniversityClient(testRestTemplate.getRestTemplate());
    }

    @AfterEach
    void tearDown() {
        partnerUniversityClient.resetDatabase();
    }

    @Test
    void testAddUniModuleToPartnerUniversity() {
        PartnerUniversity partnerUniversity = partnerUniversityClient.getSinglePartnerUniversity(1).getBody();

        UniModule uniModule1 = new UniModule(
                "module 1 name",
                1,
                1,
                partnerUniversity
        );

        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getSingleUniModule(1, 20).getStatusCode());

        ResponseEntity<UniModule> response = uniModuleClient.addNewUniModuleToPartnerUniversity(1, uniModule1);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(HttpStatus.CONFLICT, uniModuleClient.addNewUniModuleToPartnerUniversity(1, uniModule1).getStatusCode());
        assertEquals(HttpStatus.OK, uniModuleClient.getSingleUniModule(1, Math.toIntExact(response.getBody().getId())).getStatusCode());
    }


    @Test
    void testGetSingleUniModule() {
        assertEquals(HttpStatus.OK, uniModuleClient.getSingleUniModule(1, 1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getSingleUniModule(99, 99).getStatusCode());
    }

    @Test
    void getAllUniModulesFromPartnerUniversity() {
        ResponseEntity<PagedModel<UniModule>> response = uniModuleClient.getAllUniModulesFromPartnerUniversity(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().size() > 1);
    }

    @Test
    void updatePartnerUniversity() {
        ResponseEntity<UniModule> response1 = uniModuleClient.getSingleUniModule(1, 1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        UniModule oldModel = response1.getBody();
        String oldName = oldModel.getName();

        oldModel.setName("new name");
        uniModuleClient.updateUniModule(1, oldModel);

        ResponseEntity<UniModule> response2 = uniModuleClient.getSingleUniModule(1, 1);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UniModule updatedModel = response2.getBody();
        String updatedName = updatedModel.getName();

        assertNotEquals(oldName, updatedName);
    }

    @Test
    void deleteUniModule() {
        assertEquals(HttpStatus.NO_CONTENT, uniModuleClient.deleteUniModule(1, 1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.deleteUniModule(1, 1).getStatusCode());
    }
}
