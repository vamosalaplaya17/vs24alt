package org.thws.management.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thws.management.server.model.UniModule;

import java.net.URI;

/**
 * Class that utilizes RestTemplate to access the implemented backend API, methods used for tests
 */
@Component
public class UniModuleClient {
    private final String BASE_URL = "http://localhost:8080/api/v1/partner-universities/";
    private final RestTemplate restTemplate;

    @Autowired
    public UniModuleClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method for adding UniModules to PartnerUniversities
     *
     * @param partnerUniversityId ID of PartnerUniversity to add to
     * @param uniModule           UniModule to be added
     * @return ResponseEntity containing information of newly added UniModule
     */
    public ResponseEntity<UniModule> addNewUniModuleToPartnerUniversity(Long partnerUniversityId, UniModule uniModule) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules");
        ResponseEntity<UniModule> response = restTemplate.postForEntity(uri, uniModule, UniModule.class);

        return response;
    }

    /**
     * Method for fetching a single UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity to fetch from
     * @param uniModuleId         ID of UniModule to be fetched
     * @return ResponseEntity containing information of fetched UniModule
     */
    public ResponseEntity<UniModule> getSingleUniModule(Long partnerUniversityId, Long uniModuleId) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules/" + uniModuleId);
        ResponseEntity<UniModule> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                UniModule.class);

        return response;
    }

    /**
     * Method for fetching all UniModules of PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to fetch from
     * @return ResponseEntity containing information of all fetched UniModules
     */
    public ResponseEntity<PagedModel<UniModule>> getAllUniModulesFromPartnerUniversity(Long partnerUniversityId) {
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL + partnerUniversityId + "/modules")
                .queryParam("page", 0)
                .queryParam("size", Integer.MAX_VALUE)
                .build().toUri();

        ResponseEntity<PagedModel<UniModule>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedModel<UniModule>>() {
                }
        );

        return response;
    }

    /**
     * Method for updating an UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity containing module to be updated
     * @param uniModule           Data to update UniModule with
     * @return ResponseEntity containing information of updated UniModule
     */
    public ResponseEntity<UniModule> updateUniModule(Long partnerUniversityId, UniModule uniModule) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules/" + uniModule.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UniModule> request = new HttpEntity<>(uniModule, headers);

        ResponseEntity<UniModule> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                UniModule.class
        );

        return response;
    }

    /**
     * Method for deleting UniModules
     *
     * @param partnerUniversityId ID of PartnerUniversity to delete UniModule from
     * @param uniModuleId         ID of UniModule to be deleted
     * @return ResponseEntity containing information about deletion
     */
    public ResponseEntity<Void> deleteUniModule(Long partnerUniversityId, Long uniModuleId) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules/" + uniModuleId);
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
    }

    /**
     * Used to reset the database when needed
     */
    public void resetDatabase() {
        String resetUrl = "http://localhost:8080/api/v1/reset-database";
        restTemplate.postForEntity(resetUrl, null, Void.class);
    }
}
