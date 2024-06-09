package org.thws.management.unimodule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thws.management.partneruniversity.PartnerUniversity;
import org.thws.management.partneruniversity.PartnerUniversityService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api/v1/partner-universities/{partnerUniversityId}/modules")
public class UniModuleController {
    private final UniModuleService uniModuleService;
    private final UniModuleModelAssembler uniModuleModelAssembler;
    private final PartnerUniversityService partnerUniversityService;

    @Autowired
    public UniModuleController(UniModuleService uniModuleService,
                               UniModuleModelAssembler uniModuleModelAssembler,
                               PartnerUniversityService partnerUniversityService) {
        this.uniModuleService = uniModuleService;
        this.uniModuleModelAssembler = uniModuleModelAssembler;
        this.partnerUniversityService = partnerUniversityService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<UniModuleModel>> getAllUniModules(
            @PathVariable Long partnerUniversityId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Integer ects,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UniModule> uniModules;

        if (name != null && semester != null && ects != null) {
            uniModules = uniModuleService.getUniModulesByNameAndSemesterAndEcts(
                    partnerUniversityId, name.toLowerCase(), semester, ects, pageable);
        } else if (name != null && semester != null) {
            uniModules = uniModuleService.getUniModulesByNameAndSemester(
                    partnerUniversityId, name.toLowerCase(), semester, pageable);
        } else if (name != null && ects != null) {
            uniModules = uniModuleService.getUniModulesByNameAndEcts(
                    partnerUniversityId, name.toLowerCase(), ects, pageable);
        } else if (semester != null && ects != null) {
            uniModules = uniModuleService.getUniModulesBySemesterAndEcts(
                    partnerUniversityId, semester, ects, pageable);
        } else if (name != null) {
            uniModules = uniModuleService.getUniModulesByName(
                    partnerUniversityId, name.toLowerCase(), pageable);
        } else if (semester != null) {
            uniModules = uniModuleService.getUniModulesBySemester(
                    partnerUniversityId, semester, pageable);
        } else if (ects != null) {
            uniModules = uniModuleService.getUniModulesByEcts(
                    partnerUniversityId, ects, pageable);
        } else {
            uniModules = uniModuleService.getAllUniModulesByPartnerUniversity(partnerUniversityId, pageable);
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

        Link selfLink = linkTo(methodOn(UniModuleController.class))
                .withSelfRel().withRel("GET");

        Link postLink = linkTo(methodOn(UniModuleController.class).addNewUniModule(partnerUniversityId, null))
                .withRel("POST");

        pagedModel.add(selfLink);
        pagedModel.add(postLink);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> getUniModule(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId) {

        UniModule uniModule = uniModuleService.getUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId);

        if (uniModule == null) {
            return ResponseEntity.notFound().build();
        } else {
            UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(uniModule);
            return ResponseEntity.ok(uniModuleModel);
        }
    }

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

    @DeleteMapping(path = "{uniModuleId}")
    public ResponseEntity<Void> deleteUniModule(@PathVariable("partnerUniversityId") Long partnerUniversityId,
                                                @PathVariable("uniModuleId") Long uniModuleId) {
        uniModuleService.deleteUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "{uniModuleId}")
    public ResponseEntity<UniModuleModel> updateUniModel(
            @PathVariable("partnerUniversityId") Long partnerUniversityId,
            @PathVariable("uniModuleId") Long uniModuleId,
            @RequestBody UniModule uniModule) {
        UniModule updatedUniModule = uniModuleService.updateUniModuleByPartnerUniversity(partnerUniversityId, uniModuleId, uniModule);
        UniModuleModel uniModuleModel = uniModuleModelAssembler.toModel(updatedUniModule);
        return ResponseEntity.ok(uniModuleModel);
    }
}