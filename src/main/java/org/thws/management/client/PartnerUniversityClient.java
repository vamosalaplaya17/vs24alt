package org.thws.management.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thws.management.server.model.PartnerUniversity;

import java.net.URI;

/**
 * Class that utilizes RestTemplate to access the implemented backend API, methods used for tests
 */
@Component
public class PartnerUniversityClient {
    private final String BASE_URL = "http://localhost:8080/api/v1/partner-universities";
    private final RestTemplate restTemplate;

    @Autowired
    public PartnerUniversityClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method used for adding new PartnerUniversities
     *
     * @param partnerUniversity ID of PartnerUniversity to be added
     * @return ResponseEntity containing information about newly created PartnerUniversity
     */
    public ResponseEntity<PartnerUniversity> addNewPartnerUniversity(PartnerUniversity partnerUniversity) {
        URI uri = URI.create(BASE_URL);
        ResponseEntity<PartnerUniversity> response = restTemplate.postForEntity(uri, partnerUniversity, PartnerUniversity.class);

        return response;
    }

    /**
     * Method for fetching a single PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to be fetched
     * @return ResponseEntity containing information about fetched PartnerUniversity
     */
    public ResponseEntity<PartnerUniversity> getSinglePartnerUniversity(Long partnerUniversityId) {
        URI uri = URI.create(BASE_URL + "/" + partnerUniversityId);
        ResponseEntity<PartnerUniversity> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                PartnerUniversity.class);

        return response;
    }

    /**
     * Method for fetching all PartnerUniversities
     *
     * @return ResponseEntity containing information about fetched PartnerUniversities
     */
    public ResponseEntity<PagedModel<PartnerUniversity>> getAllPartnerUniversities() {
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("page", 0)
                .queryParam("size", Integer.MAX_VALUE)
                .build().toUri();

        ResponseEntity<PagedModel<PartnerUniversity>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedModel<PartnerUniversity>>() {
                }
        );

        return response;
    }

    /**
     * Method used for fetching PartnerUniversities by filters
     *
     * @param name           Name of PartnerUniversity to be filtered by
     * @param country        Country of PartnerUniversity to be filtered by
     * @param departmentName Department Name of PartnerUniversity to be filtered by
     * @return ResponseEntity containing information about fetched PartnerUniversities
     */
    public ResponseEntity<PagedModel<PartnerUniversity>> getAllPartnerUniversitiesByFilters(String name, String country, String departmentName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("page", 0)
                .queryParam("size", Integer.MAX_VALUE);

        if (name != null) {
            builder.queryParam("name", name);
        }
        if (country != null) {
            builder.queryParam("country", country);
        }
        if (departmentName != null) {
            builder.queryParam("departmentName", departmentName);
        }

        URI uri = builder.build().toUri();

        ResponseEntity<PagedModel<PartnerUniversity>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedModel<PartnerUniversity>>() {
                }
        );

        return response;
    }

    /**
     * Method for updating a PartnerUniversity
     *
     * @param partnerUniversity Data to be used for updating PartnerUniversity
     * @return ResponseEntity containing information about updated PartnerUniversity
     */
    public ResponseEntity<PartnerUniversity> updatePartnerUniversity(PartnerUniversity partnerUniversity) {
        URI uri = URI.create(BASE_URL + "/" + partnerUniversity.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PartnerUniversity> request = new HttpEntity<>(partnerUniversity, headers);

        ResponseEntity<PartnerUniversity> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                PartnerUniversity.class
        );

        return response;
    }

    /**
     * Method for deleting PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to be deleted
     * @return ResponseEntity containing information about deleted PartnerUniversity
     */
    public ResponseEntity<Void> deletePartnerUniversity(Long partnerUniversityId) {
        URI uri = URI.create(BASE_URL + "/" + partnerUniversityId);
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
    }

    /**
     * Method for resetting the database
     */
    public void resetDatabase() {
        String resetUrl = "http://localhost:8080/api/v1/reset-database";
        restTemplate.postForEntity(resetUrl, null, Void.class);
    }
}
