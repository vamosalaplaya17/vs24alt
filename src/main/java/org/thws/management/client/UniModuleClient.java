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
import org.thws.management.server.model.UniModule;
import org.thws.management.server.model.UniModuleModel;

import java.net.URI;

@Component
public class UniModuleClient {
    private final String BASE_URL = "http://localhost:8080/api/v1/partner-universities/";
    private final RestTemplate restTemplate;

    @Autowired
    public UniModuleClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<UniModule> addNewUniModuleToPartnerUniversity(int partnerUniversityId, UniModule uniModule) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules");
        ResponseEntity<UniModule> response = restTemplate.postForEntity(uri, uniModule, UniModule.class);

        return response;
    }

    public ResponseEntity<UniModule> getSingleUniModule(int partnerUniversityId, int uniModuleId) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules/" + uniModuleId);
        ResponseEntity<UniModule> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                UniModule.class);

        return response;
    }

    public ResponseEntity<PagedModel<UniModule>> getAllUniModulesFromPartnerUniversity(int partnerUniversityId) {
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

    public ResponseEntity<Void> deleteUniModule(int partnerUniversityId, int uniModuleId) {
        URI uri = URI.create(BASE_URL + partnerUniversityId + "/modules/" + uniModuleId);
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
    }

    public ResponseEntity<UniModule> updateUniModule(int partnerUniversityId, UniModule uniModule) {
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

    public ResponseEntity<PagedModel<UniModule>> getAllUniModulesByFilters(int partnerUniversityId, String name, Integer semester, Integer ects) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + partnerUniversityId + "/modules")
                .queryParam("page", 0)
                .queryParam("size", Integer.MAX_VALUE);

        if (name != null) {
            builder.queryParam("name", name);
        }
        if (semester != null) {
            builder.queryParam("semester", semester);
        }
        if (ects != null) {
            builder.queryParam("ects", ects);
        }

        URI uri = builder.build().toUri();

        ResponseEntity<PagedModel<UniModule>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedModel<UniModule>>() {
                }
        );

        return response;
    }
}
