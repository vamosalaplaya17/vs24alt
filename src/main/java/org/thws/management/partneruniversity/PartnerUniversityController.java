package org.thws.management.partneruniversity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "api/v1/partner-universities")
public class PartnerUniversityController {

    private final PartnerUniversityService partnerUniversityService;
    private final PartnerUniversityModelAssembler partnerUniversityModelAssembler;

    @Autowired
    public PartnerUniversityController(PartnerUniversityService partnerUniversityService, PartnerUniversityModelAssembler partnerUniversityModelAssembler) {
        this.partnerUniversityService = partnerUniversityService;
        this.partnerUniversityModelAssembler = partnerUniversityModelAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<PartnerUniversityModel>> getPartnerUniversities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String departmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PartnerUniversity> partnerUniversities;

        if (name != null && country != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndCountryAndDepartmentName(
                    name.toLowerCase(), country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null && country != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndCountry(
                    name.toLowerCase(), country.toLowerCase(), pageable);
        } else if (name != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndDepartmentName(
                    name.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (country != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByCountryAndDepartmentName(
                    country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByName(
                    name.toLowerCase(), pageable);
        } else if (country != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByCountry(
                    country.toLowerCase(), pageable);
        } else if (departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByDepartmentName(
                    departmentName.toLowerCase(), pageable);
        } else {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversities(pageable);
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

        Link selfLink = linkTo(PartnerUniversityController.class).withSelfRel().withRel("GET");

        Link postLink = linkTo(methodOn(PartnerUniversityController.class).addNewPartnerUniversity(null)).withRel("POST");

        pagedModel.add(selfLink);
        pagedModel.add(postLink);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping(path = "{partneruniversityId}")
    public ResponseEntity<PartnerUniversityModel> getPartnerUniversity(@PathVariable("partneruniversityId") Long
                                                                               partnerUniversityId) {
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);

        if (partnerUniversity == null) {
            return ResponseEntity.notFound().build();
        } else {
            PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(partnerUniversity);

            Link putLink = linkTo(methodOn(PartnerUniversityController.class)
                    .updatePartnerUniversity(partnerUniversityId, null)).withRel("PUT");
            Link deleteLink = linkTo(methodOn(PartnerUniversityController.class)
                    .deletePartnerUniversity(partnerUniversityId)).withRel("DELETE");

            partnerUniversityModel.add(putLink);
            partnerUniversityModel.add(deleteLink);

            return ResponseEntity.ok(partnerUniversityModel);
        }
    }

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

    @DeleteMapping(path = "{partneruniversityId}")
    public ResponseEntity<Void> deletePartnerUniversity(@PathVariable("partneruniversityId") Long partneruniversityId) {
        partnerUniversityService.deletePartnerUniversity(partneruniversityId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "{partneruniversityId}")
    public ResponseEntity<PartnerUniversityModel> updatePartnerUniversity(
            @PathVariable("partneruniversityId") Long partneruniversityId,
            @RequestBody PartnerUniversity partnerUniversity) {

        PartnerUniversity updatePartnerUniversity = partnerUniversityService.updatePartnerUniversity(partneruniversityId, partnerUniversity);
        PartnerUniversityModel partnerUniversityModel = partnerUniversityModelAssembler.toModel(updatePartnerUniversity);

        return ResponseEntity.ok(partnerUniversityModel);
    }
}
