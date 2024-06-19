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
import org.thws.management.client.UniModuleClient;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.UniModule;

import java.time.LocalDate;

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

    @Test
    void testGetSingleUniModule() {
        assertEquals(HttpStatus.OK, uniModuleClient.getSingleUniModule(1, 1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getSingleUniModule(99, 99).getStatusCode());
    }

    @Test
    void getAllUniModulesFromPartnerUniversity() {
        assertEquals(HttpStatus.OK, uniModuleClient.getAllUniModulesFromPartnerUniversity(1).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, uniModuleClient.deleteUniModule(1, 1).getStatusCode());
        assertEquals(HttpStatus.OK, uniModuleClient.getAllUniModulesFromPartnerUniversity(1).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, uniModuleClient.deleteUniModule(1, 2).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.getAllUniModulesFromPartnerUniversity(1).getStatusCode());
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
    void deleteUniModule() {
        assertEquals(HttpStatus.NO_CONTENT, uniModuleClient.deleteUniModule(1,1).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.deleteUniModule(1,1).getStatusCode());
    }

    @Test
    void updatePartnerUniversity() {
        ResponseEntity<UniModule> response1 = uniModuleClient.getSingleUniModule(2, 3);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        UniModule oldModel = response1.getBody();
        String oldName = oldModel.getName();

        oldModel.setName("new name");
        uniModuleClient.updateUniModule(2, oldModel);

        ResponseEntity<UniModule> response2 = uniModuleClient.getSingleUniModule(2, 3);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UniModule updatedModel = response2.getBody();
        String updatedName = updatedModel.getName();

        assertNotEquals(oldName, updatedName);

        ResponseEntity<UniModule> response3 = uniModuleClient.getSingleUniModule(2, 3);
        UniModule deletedModel = response3.getBody();
        deletedModel.setName("deleted name");
        uniModuleClient.deleteUniModule(2, 3);
        assertEquals(HttpStatus.NOT_FOUND, uniModuleClient.updateUniModule(2, deletedModel).getStatusCode());
    }

    @Test
    void testGetPartnerUniversityByFilters() {
        PartnerUniversity template = new PartnerUniversity(
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

        ResponseEntity<PartnerUniversity> response = partnerUniversityClient.addNewPartnerUniversity(template);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PartnerUniversity university = response.getBody();

        UniModule uniModule1 = new UniModule(
                "filter 1 name",
                3,
                2,
                university
        );

        UniModule uniModule2 = new UniModule(
                "filter 2 name",
                3,
                2,
                university
        );

        UniModule uniModule3 = new UniModule(
                "filter 3 name",
                3,
                1,
                university
        );

        int universityId = Math.toIntExact(response.getBody().getId());

        uniModuleClient.addNewUniModuleToPartnerUniversity(universityId, uniModule1);
        uniModuleClient.addNewUniModuleToPartnerUniversity(universityId, uniModule2);
        uniModuleClient.addNewUniModuleToPartnerUniversity(universityId, uniModule3);

        ResponseEntity<PagedModel<UniModule>> response1 = uniModuleClient.getAllUniModulesByFilters(universityId,
                "filter 1 name", null, null);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        PagedModel<UniModule> modules1 = response1.getBody();
        assertEquals(1, modules1.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response2 = uniModuleClient.getAllUniModulesByFilters(universityId,
                null, 3, null);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        PagedModel<UniModule> modules2 = response2.getBody();
        assertEquals(3, modules2.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response3 = uniModuleClient.getAllUniModulesByFilters(universityId,
                null, null, 2);
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        PagedModel<UniModule> modules3 = response3.getBody();
        assertEquals(2, modules3.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response4 = uniModuleClient.getAllUniModulesByFilters(universityId,
                "filter 1 name", 3, null);
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        PagedModel<UniModule> modules4 = response4.getBody();
        assertEquals(1, modules4.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response5 = uniModuleClient.getAllUniModulesByFilters(universityId,
                "filter 2 name", null, 2);
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        PagedModel<UniModule> modules5 = response5.getBody();
        assertEquals(1, modules5.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response6 = uniModuleClient.getAllUniModulesByFilters(universityId,
                null, 3, 2);
        assertEquals(HttpStatus.OK, response6.getStatusCode());

        PagedModel<UniModule> modules6 = response6.getBody();
        assertEquals(2, modules6.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response7 = uniModuleClient.getAllUniModulesByFilters(universityId,
                "filter 3 name", 3, 1);
        assertEquals(HttpStatus.OK, response7.getStatusCode());

        PagedModel<UniModule> modules7 = response7.getBody();
        assertEquals(1, modules7.getContent().size());


        ResponseEntity<PagedModel<UniModule>> response8 = uniModuleClient.getAllUniModulesByFilters(universityId,
                "non existent", 17, 3);
        assertEquals(HttpStatus.NOT_FOUND, response8.getStatusCode());
    }
}
