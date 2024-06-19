package org.thws.management.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.PartnerUniversityModel;

import java.net.URI;

@Component
public class PartnerUniversityClient {
    private final String BASE_URL = "http://localhost:8080/api/v1/partner-universities";
    private final RestTemplate restTemplate;

    @Autowired
    public PartnerUniversityClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<PartnerUniversity> addNewPartnerUniversity(PartnerUniversity partnerUniversity) {
        URI uri = URI.create(BASE_URL);
        ResponseEntity<PartnerUniversity> response = restTemplate.postForEntity(uri, partnerUniversity, PartnerUniversity.class);

        return response;
    }

    public ResponseEntity<PartnerUniversity> getSinglePartnerUniversity(int id) {
        URI uri = URI.create(BASE_URL + "/" + id);
        ResponseEntity<PartnerUniversity> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                PartnerUniversity.class);

        return response;
    }

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

    public ResponseEntity<Void> deletePartnerUniversity(int id) {
        URI uri = URI.create(BASE_URL + "/" + id);
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
    }
}
