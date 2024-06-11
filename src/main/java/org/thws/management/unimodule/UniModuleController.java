package org.thws.management.unimodule;

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
import org.thws.management.partneruniversity.PartnerUniversity;
import org.thws.management.partneruniversity.PartnerUniversityService;

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
     * @return ResponseEntity containing newly created UniModule
     */
    @PostMapping
    public ResponseEntity<UniModuleModel> addNewUniModule(@PathVariable Long partnerUniversityId,
                                                          @RequestBody UniModule uniModule) {
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);
        uniModule.setPartnerUniversity(partnerUniversity);

        UniModule savedUniModule = uniModuleService.addNewUniModule(partnerUniversityId, uniModule);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(savedUniModule);

        return ResponseEntity
                .created(linkTo(
                        methodOn(UniModuleController.class)
                                .getUniModule(savedUniModule.getId(), partnerUniversityId))
                        .toUri())
                .body(uniModuleModel);
    }

    /**
     * Gets all UniModules, divided in pages, and creates related links
     * Potentially filtered by any combination of name, semester and ects
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve UniModules from
     * @param name                Name of the UniModules to be filtered by
     * @param semester            Semester of the UniModules to be filtered by
     * @param ects                Credits of the UniModules to be filtered by
     * @param page                Page number to retrieve, default value is 0
     * @param size                Number of total UniModules per page, default is 2 (to make testing easier)
     * @return Page of UniModule
     * @throws ResponseStatusException If no UniModules are found
     */
    @GetMapping
    public ResponseEntity<PagedModel<UniModuleModel>> getAllUniModules(
            @PathVariable Long partnerUniversityId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Integer ects,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        Page<UniModule> uniModules;
        Pageable pageable = PageRequest.of(page, size);

        if (name != null || semester != null || ects != null) {
            uniModules = uniModuleService.getAllUniModulesByPartnerUniversityWithFilters(
                    partnerUniversityId, name, semester, ects, pageable);
        } else {
            uniModules = uniModuleService.getAllUniModulesByPartnerUniversity(partnerUniversityId, pageable);
        }

        if (uniModules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no uni modules found");
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

        Link selfLink = linkTo(methodOn(UniModuleController.class).getAllUniModules(partnerUniversityId, name, semester, ects, page, size))
                .withSelfRel().withType("GET");

        Link postLink = linkTo(methodOn(UniModuleController.class).addNewUniModule(partnerUniversityId, null))
                .withRel("create").withType("POST");

        pagedModel.add(selfLink);
        pagedModel.add(postLink);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Get one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve specific UniModule from
     * @param uniModuleId         ID of UniModule to get
     * @return ResponseEntity of requested UniModule
     */
    @GetMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> getUniModule(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId) {

        UniModule uniModule = uniModuleService.getUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(uniModule);
        return ResponseEntity.ok(uniModuleModel);
    }

    /**
     * Updates one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity whose UniModule is to be updated
     * @param uniModuleId         ID of UniModule to update
     * @param uniModule           Content used to update UniModule
     * @return ResponseEntity of updated UniModule
     */
    @PutMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> updateUniModule(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId,
            @RequestBody UniModule uniModule) {
        UniModule updatedUniModule = uniModuleService.updateUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId, uniModule);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(updatedUniModule);
        return ResponseEntity.ok(uniModuleModel);
    }

    /**
     * Deletes one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity where UniModule shall be deleted from
     * @param uniModuleId         ID of UniModule to be deleted
     * @return ResponseEntity with status code 204 No Content
     */
    @DeleteMapping(path = "{uniModuleId}")
    public ResponseEntity<Void> deleteUniModule(@PathVariable("partnerUniversityId") Long partnerUniversityId,
                                                @PathVariable("uniModuleId") Long uniModuleId) {
        uniModuleService.deleteUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId);
        return ResponseEntity.noContent().build();
    }
}