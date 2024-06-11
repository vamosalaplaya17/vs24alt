package org.thws.management.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.PartnerUniversityModel;
import org.thws.management.server.assembler.PartnerUniversityModelAssembler;
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
     * @return ResponseEntity containing the new PartnerUniversity
     */
    @PostMapping
    public ResponseEntity<PartnerUniversityModel> addNewPartnerUniversity(@RequestBody PartnerUniversity partnerUniversity) {
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
     * Retrieves every PartnerUniversity available and creates related links
     * If name, country and departmentName are set, it filters the PartnerUniversities accordingly
     *
     * @param name           Name of PartnerUniversity
     * @param country        Country of PartnerUniversity
     * @param departmentName Department name of PartnerUniversity
     * @param page           Page number to retrieve, default is 0
     * @param size           Number of PartnerUniversities to show per page, standard is 3 (to make testing easier)
     * @return Page containing PartnerUniversities
     * @throws ResponseStatusException if there are no PartnerUniversities to show
     */
    @GetMapping
    public ResponseEntity<PagedModel<PartnerUniversityModel>> getPartnerUniversities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String departmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Page<PartnerUniversity> partnerUniversities;
        Pageable pageable = PageRequest.of(page, size);

        if (name != null || country != null || departmentName != null) {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversitiesWithFilters(
                    name, country, departmentName, pageable);
        } else {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversities(pageable);
        }

        if (partnerUniversities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no partner universities found");
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

        Link selfLink = linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversities(name, country, departmentName, page, size))
                .withSelfRel().withType("GET");

        Link postLink = linkTo(methodOn(PartnerUniversityController.class).addNewPartnerUniversity(null))
                .withRel("create").withType("POST");

        pagedModel.add(selfLink);
        pagedModel.add(postLink);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Retrieves one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve
     * @return ResponseEntity containing model of requested PartnerUniversity
     */
    @GetMapping(path = "{partnerUniversityId}")
    public ResponseEntity<PartnerUniversityModel> getPartnerUniversity(
            @PathVariable("partnerUniversityId") Long partnerUniversityId) {

        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(partnerUniversity);

        return ResponseEntity.ok(partnerUniversityModel);
    }

    /**
     * Updates one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to update
     * @param partnerUniversity   Content to update PartnerUniversity with
     * @return ResponseEntity containing model of updated PartnerUniversity
     */
    @PutMapping(path = "{partnerUniversityId}")
    public ResponseEntity<PartnerUniversityModel> updatePartnerUniversity(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @RequestBody PartnerUniversity partnerUniversity) {

        PartnerUniversity updatePartnerUniversity = partnerUniversityService.updatePartnerUniversity(partnerUniversityId, partnerUniversity);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(updatePartnerUniversity);

        return ResponseEntity.ok(partnerUniversityModel);
    }

    /**
     * Deletes one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to delete
     * @return Status Code 204 upon successful deletion
     */
    @DeleteMapping(path = "{partnerUniversityId}")
    public ResponseEntity<Void> deletePartnerUniversity(@PathVariable("partnerUniversityId") Long partnerUniversityId) {
        partnerUniversityService.deletePartnerUniversity(partnerUniversityId);
        return ResponseEntity.noContent().build();
    }
}