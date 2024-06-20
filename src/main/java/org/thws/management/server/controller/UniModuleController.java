package org.thws.management.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thws.management.server.assembler.UniModuleModelAssembler;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.UniModule;
import org.thws.management.server.model.UniModuleModel;
import org.thws.management.server.repository.UniModuleRepository;
import org.thws.management.server.service.PartnerUniversityService;
import org.thws.management.server.service.UniModuleService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller class to handle HTTP Requests regarding UniModules
 */
@RestController
@RequestMapping(path = "/api/v1/partner-universities/{partnerUniversityId}/modules")
public class UniModuleController {
    private final UniModuleService uniModuleService;
    private final UniModuleModelAssembler uniModuleModelAssembler;
    private final PartnerUniversityService partnerUniversityService;

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "3";
    public static final String DEFAULT_SORT = "asc";

    /**
     * Constructs a new UniModuleController
     *
     * @param uniModuleService         Service used to handle UniModule operations
     * @param uniModuleModelAssembler  Assembler used to convert UniModules to their model representation
     * @param partnerUniversityService Service used to handle PartnerUniversity operations
     */
    @Autowired
    public UniModuleController(UniModuleService uniModuleService,
                               UniModuleModelAssembler uniModuleModelAssembler,
                               PartnerUniversityService partnerUniversityService) {
        this.uniModuleService = uniModuleService;
        this.uniModuleModelAssembler = uniModuleModelAssembler;
        this.partnerUniversityService = partnerUniversityService;
    }

    /**
     * Creates a new UniModule for a specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to create UniModule for
     * @param uniModule           UniModule body
     * @return Status code 200 and ResponseEntity containing added UniModule
     * Status code 404 if requested PartnerUniversity does not exist
     * Status code 400 if UniModule to add is wrongly formatted
     */
    @PostMapping
    public ResponseEntity<UniModuleModel> addNewUniModule(@PathVariable Long partnerUniversityId,
                                                          @RequestBody UniModule uniModule) {
        if (partnerUniversityService.getPartnerUniversityById(partnerUniversityId) == null) {
            return ResponseEntity.notFound().build();
        }
        if (uniModule.getName() == null || uniModule.getName().isEmpty() ||
                uniModule.getSemester() == null ||
                uniModule.getEcts() == null) return ResponseEntity.badRequest().build();

        HttpHeaders headers = getHeadersForSingleUniModule(partnerUniversityId, uniModule.getId());

        UniModule savedUniModule = uniModuleService.addNewUniModule(partnerUniversityId, uniModule);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(savedUniModule);

        return ResponseEntity
                .created(linkTo(
                        methodOn(UniModuleController.class)
                                .getUniModule(savedUniModule.getId(), partnerUniversityId))
                        .toUri())
                .headers(headers).body(uniModuleModel);
    }

    /**
     * Fetch one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve specific UniModule from
     * @param uniModuleId         ID of UniModule to get
     * @return ResponseEntity of requested UniModule with status code 200
     * Status code 404 if nothing is found
     */
    @GetMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> getUniModule(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId) {
        if (checkIfNull(partnerUniversityId, uniModuleId)) return ResponseEntity.notFound().build();

        HttpHeaders headers = getHeadersForSingleUniModule(partnerUniversityId, uniModuleId);

        UniModule uniModule = uniModuleService.getUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(uniModule);

        return ResponseEntity.ok().headers(headers).body(uniModuleModel);
    }

    /**
     * Gets all UniModules, divided in pages
     * Potentially sorted by asc or desc
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve UniModules from
     * @param page                Page number to retrieve, default value is 0
     * @param size                Number of total UniModules per page, default is 2 (to make testing easier)
     * @param sort                Sorts the UniModules by name, having ascending as the default value
     * @return Page of UniModule with status code 200
     * Status code 404 if no UniModule is found
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedModel<UniModuleModel>> getAllUniModules(
            @PathVariable Long partnerUniversityId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(required = false, defaultValue = DEFAULT_SORT) String sort) {

        Page<UniModule> uniModules;

        Sort.Direction sortDirection = sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObject = Sort.by(sortDirection, "name");

        Pageable pageable = PageRequest.of(page, size, sortObject);

        uniModules = uniModuleService.getAllUniModulesByPartnerUniversity(partnerUniversityId, pageable);

        if (uniModules.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UniModuleModel> uniModuleModels = uniModules.getContent().stream()
                .map(uniModuleModelAssembler::toModel)
                .toList();

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                uniModules.getSize(),
                uniModules.getNumber(),
                uniModules.getTotalElements(),
                uniModules.getTotalPages()
        );

        PagedModel<UniModuleModel> pagedModel = PagedModel.of(uniModuleModels, pageMetadata);

        Link selfLink = linkTo(methodOn(UniModuleController.class).getAllUniModules(partnerUniversityId, page, size, sort))
                .withSelfRel().withType("GET");
        pagedModel.add(selfLink);

        HttpHeaders headers = new HttpHeaders();

        Link postLink = linkTo(methodOn(UniModuleController.class).addNewUniModule(partnerUniversityId, null))
                .withRel("create").withType("POST");
        headers.add("create", postLink.getHref());

        if (!sort.equalsIgnoreCase("asc")) {
            Link selfLinkAsc = linkTo(methodOn(UniModuleController.class)
                    .getAllUniModules(partnerUniversityId, page, size, "asc"))
                    .withRel("sort descending").withType("GET");
            pagedModel.add(selfLinkAsc);
        }

        if (!sort.equalsIgnoreCase("desc")) {
            Link selfLinkDesc = linkTo(methodOn(UniModuleController.class)
                    .getAllUniModules(partnerUniversityId, page, size, "asc"))
                    .withRel("sort ascending").withType("GET");
            pagedModel.add(selfLinkDesc);
        }

        if (uniModules.hasPrevious()) {
            Link prevLink = linkTo(methodOn(UniModuleController.class).getAllUniModules(partnerUniversityId, page - 1, size, sort))
                    .withRel("previous").withType("GET");
            headers.add("previous page", prevLink.getHref());
        }

        if (uniModules.hasNext()) {
            Link nextLink = linkTo(methodOn(UniModuleController.class).getAllUniModules(partnerUniversityId, page - 1, size, sort)).withSelfRel()
                    .withRel("next").withType("GET");
            headers.add("next page", nextLink.getHref());
        }

        Link partnerUniversityLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversity(partnerUniversityId))
                .withRel("partnerUniversity").withType("GET");
        headers.add("partner-university", partnerUniversityLink.getHref());

        return ResponseEntity.ok().headers(headers).body(pagedModel);
    }

    /**
     * Updates one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity whose UniModule is to be updated
     * @param uniModuleId         ID of UniModule to update
     * @param uniModule           Content used to update UniModule
     * @return ResponseEntity of updated UniModule with status code 200
     * Status code 404 if nothing is found, Status code 400 if request is wrongly formatted
     */
    @PutMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> updateUniModule(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId,
            @RequestBody UniModule uniModule) {
        if (checkIfNull(partnerUniversityId, uniModuleId)) return ResponseEntity.notFound().build();

        HttpHeaders headers = getHeadersForSingleUniModule(partnerUniversityId, uniModuleId);

        UniModule updatedUniModule = uniModuleService.updateUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId, uniModule);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(updatedUniModule);
        return ResponseEntity.ok().headers(headers).body(uniModuleModel);
    }

    /**
     * Deletes one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity where UniModule shall be deleted from
     * @param uniModuleId         ID of UniModule to be deleted
     * @return ResponseEntity with status code 204 No Content
     * Status code 404 if nothing is found
     */
    @DeleteMapping(path = "{uniModuleId}")
    public ResponseEntity<Void> deleteUniModule(@PathVariable("partnerUniversityId") Long partnerUniversityId,
                                                @PathVariable("uniModuleId") Long uniModuleId) {
        if (checkIfNull(partnerUniversityId, uniModuleId)) return ResponseEntity.notFound().build();

        HttpHeaders headers = getHeadersForSingleUniModule(partnerUniversityId, uniModuleId);

        uniModuleService.deleteUniModuleByPartnerUniversity(uniModuleId);
        return ResponseEntity.noContent().headers(headers).build();
    }

    /**
     * Builds links in the Response Header for requests containing only a single UniModule
     *
     * @param partnerUniversityId ID of the PartnerUniversity
     * @param uniModuleId         ID of the UniModule
     * @return Links for updating and deleting an UniModule, link for going back to /modules,
     * and link to go back to PartnerUniversity
     */
    public HttpHeaders getHeadersForSingleUniModule(Long partnerUniversityId, Long uniModuleId) {
        HttpHeaders headers = new HttpHeaders();

        Link updateLink = linkTo(methodOn(UniModuleController.class).getUniModule(partnerUniversityId, uniModuleId))
                .withRel("update").withType("PUT");
        headers.add("update", updateLink.getHref());

        Link deleteLink = linkTo(methodOn(UniModuleController.class).deleteUniModule(partnerUniversityId, uniModuleId))
                .withRel("delete").withType("DELETE");
        headers.add("delete", deleteLink.getHref());

        Link allModulesLink = linkTo(methodOn(UniModuleController.class)
                .getAllUniModules(partnerUniversityId, Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_SIZE), DEFAULT_SORT))
                .withRel("modules").withType("GET");
        headers.add("modules", allModulesLink.getHref());

        Link partnerUniversityLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversity(partnerUniversityId))
                .withRel("partnerUniversity").withType("GET");
        headers.add("partner-university", partnerUniversityLink.getHref());

        return headers;
    }

    /**
     * Method to check if requested PartnerUniversity and UniModule exists
     *
     * @param partnerUniversityId ID of PartnerUniversity
     * @param uniModuleId         ID of UniModule
     * @return true if nothing is found, otherwise false
     */
    public boolean checkIfNull(Long partnerUniversityId, Long uniModuleId) {
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);
        UniModule uniModule = uniModuleService.getUniModuleById(uniModuleId);

        return partnerUniversity == null || uniModule == null;
    }
}