package org.thws.management.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thws.management.server.assembler.PartnerUniversityModelAssembler;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.PartnerUniversityModel;
import org.thws.management.server.service.PartnerUniversityService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller class to handle HTTP Requests regarding PartnerUniversities
 */
@RestController
@RequestMapping(path = "api/v1/partner-universities")
public class PartnerUniversityController {

    private final PartnerUniversityService partnerUniversityService;
    private final PartnerUniversityModelAssembler partnerUniversityModelAssembler;

    /**
     * Constructs a new PartnerUniversityController
     *
     * @param partnerUniversityService        Service used to handle PartnerUniversity operations
     * @param partnerUniversityModelAssembler Assembler used to convert PartnerUniversities to their model representations
     */
    @Autowired
    public PartnerUniversityController(PartnerUniversityService partnerUniversityService, PartnerUniversityModelAssembler partnerUniversityModelAssembler) {
        this.partnerUniversityService = partnerUniversityService;
        this.partnerUniversityModelAssembler = partnerUniversityModelAssembler;
    }

    /**
     * Creates a new PartnerUniversity
     *
     * @param partnerUniversity PartnerUniversity data to be used to create a new PartnerUniversity
     * @return ResponseEntity containing the new PartnerUniversity with status code 201
     * Status code 400 if request body is wrongly formatted
     */
    @PostMapping
    public ResponseEntity<PartnerUniversityModel> addNewPartnerUniversity(@RequestBody PartnerUniversity partnerUniversity) {
        if (partnerUniversity.getName() == null || partnerUniversity.getName().isEmpty() ||
                partnerUniversity.getCountry() == null || partnerUniversity.getCountry().isEmpty() ||
                partnerUniversity.getDepartmentName() == null || partnerUniversity.getDepartmentName().isEmpty() ||
                partnerUniversity.getDepartmentUrl() == null || partnerUniversity.getDepartmentUrl().isEmpty() ||
                partnerUniversity.getContactPerson() == null || partnerUniversity.getContactPerson().isEmpty() ||
                partnerUniversity.getMaxStudentsIn() == null ||
                partnerUniversity.getMaxStudentsOut() == null ||
                partnerUniversity.getNextSpringSemester() == null ||
                partnerUniversity.getNextSummerSemester() == null) return ResponseEntity.badRequest().build();

        PartnerUniversity savedPartnerUniversity = partnerUniversityService.addNewPartnerUniversity(partnerUniversity);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(savedPartnerUniversity);

        return ResponseEntity
                .created(linkTo(
                        methodOn(PartnerUniversityController.class)
                                .getPartnerUniversity(savedPartnerUniversity.getId()))
                        .toUri())
                .body(partnerUniversityModel);
    }

    /**
     * Retrieves one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve
     * @return ResponseEntity containing model of requested PartnerUniversity with status code 200
     * Status code 404 if requested PartnerUniversity does not exist
     */
    @GetMapping(path = "{partnerUniversityId}")
    public ResponseEntity<PartnerUniversityModel> getPartnerUniversity(
            @PathVariable("partnerUniversityId") Long partnerUniversityId) {

        if (partnerUniversityService.getPartnerUniversityById(partnerUniversityId) == null) {
            return ResponseEntity.notFound().build();
        }
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(partnerUniversity);

        HttpHeaders headers = new HttpHeaders();

        Link updateLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversity(partnerUniversityId))
                .withSelfRel().withType("PUT");
        headers.add("update", updateLink.getHref());

        Link deleteLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversity(partnerUniversityId))
                .withSelfRel().withType("DELETE");
        headers.add("delete", deleteLink.getHref());

        return ResponseEntity.ok().headers(headers).body(partnerUniversityModel);
    }

    /**
     * Retrieves every PartnerUniversity available and creates related links
     * If name, country and departmentName are set, it filters the PartnerUniversities accordingly
     *
     * @param name           Name of PartnerUniversity
     * @param country        Country of PartnerUniversity
     * @param departmentName Department name of PartnerUniversity
     * @param page           Page number to retrieve, default is 0
     * @param size           Number of PartnerUniversities to show per page, standard is 2 (to make testing easier)
     * @return Page containing PartnerUniversities with status code 200
     * Status code 404 if it finds nothing
     */
    @GetMapping
    public ResponseEntity<PagedModel<PartnerUniversityModel>> getPartnerUniversities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String departmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false, defaultValue = "asc") String sort) {

        Page<PartnerUniversity> partnerUniversities;

        Sort.Direction sortDirection = sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObject = Sort.by(sortDirection, "name");

        Pageable pageable = PageRequest.of(page, size, sortObject);

        if (name != null || country != null || departmentName != null) {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversitiesWithFilters(
                    name, country, departmentName, pageable);
        } else {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversities(pageable);
        }

        if (partnerUniversities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<PartnerUniversityModel> partnerUniversityModels = partnerUniversities.getContent().stream()
                .map(partnerUniversityModelAssembler::toModel)
                .toList();

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                partnerUniversities.getSize(),
                partnerUniversities.getNumber(),
                partnerUniversities.getTotalElements(),
                partnerUniversities.getTotalPages()
        );

        PagedModel<PartnerUniversityModel> pagedModel = PagedModel.of(partnerUniversityModels, pageMetadata);

        Link selfLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversities(name, country, departmentName, page, size, sort))
                .withSelfRel().withType("GET");
        pagedModel.add(selfLink);

        HttpHeaders headers = new HttpHeaders();

        Link postLink = linkTo(methodOn(PartnerUniversityController.class).addNewPartnerUniversity(null))
                .withRel("create").withType("POST");
        headers.add("create", postLink.getHref());

        if (!sort.equalsIgnoreCase("asc")) {
            Link selfLinkAsc = linkTo(methodOn(PartnerUniversityController.class)
                    .getPartnerUniversities(name, country, departmentName, page, size, "asc"))
                    .withRel("sort").withType("GET");
            headers.add("sort ascending", selfLinkAsc.getHref());
        }

        if (!sort.equalsIgnoreCase("desc")) {
            Link selfLinkDesc = linkTo(methodOn(PartnerUniversityController.class)
                    .getPartnerUniversities(name, country, departmentName, page, size, "desc"))
                    .withRel("sort").withType("GET");
            headers.add("sort descending", selfLinkDesc.getHref());
        }

        if (partnerUniversities.hasPrevious()) {
            Link prevLink = linkTo(methodOn(PartnerUniversityController.class)
                    .getPartnerUniversities(name, country, departmentName, page - 1, size, sort))
                    .withRel("previous").withType("GET");
            headers.add("previous page", prevLink.getHref());
        }

        if (partnerUniversities.hasNext()) {
            Link nextLink = linkTo(methodOn(PartnerUniversityController.class)
                    .getPartnerUniversities(name, country, departmentName, page + 1, size, sort))
                    .withRel("next").withType("GET");
            headers.add("next page", nextLink.getHref());
        }

        return ResponseEntity.ok().headers(headers).body(pagedModel);
    }

    /**
     * Updates one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to update
     * @param partnerUniversity   Content to update PartnerUniversity with
     * @return ResponseEntity containing model of updated PartnerUniversity with status code 200
     * Status code 404 if it doesn't find requested PartnerUniversity
     */
    @PutMapping(path = "{partnerUniversityId}")
    public ResponseEntity<PartnerUniversityModel> updatePartnerUniversity(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @RequestBody PartnerUniversity partnerUniversity) {
        if (partnerUniversityService.getPartnerUniversityById(partnerUniversityId) == null) {
            return ResponseEntity.notFound().build();
        }

        PartnerUniversity updatePartnerUniversity = partnerUniversityService.updatePartnerUniversity(partnerUniversityId, partnerUniversity);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(updatePartnerUniversity);

        return ResponseEntity.ok(partnerUniversityModel);
    }

    /**
     * Deletes one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to delete
     * @return Status Code 204 upon successful deletion
     * Status code 404 if it can't find requested PartnerUniversity
     */
    @DeleteMapping(path = "{partnerUniversityId}")
    public ResponseEntity<Void> deletePartnerUniversity(@PathVariable("partnerUniversityId") Long partnerUniversityId) {
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);
        if (partnerUniversity == null) {
            return ResponseEntity.notFound().build();
        }

        partnerUniversityService.deletePartnerUniversity(partnerUniversityId);
        return ResponseEntity.noContent().build();
    }
}
